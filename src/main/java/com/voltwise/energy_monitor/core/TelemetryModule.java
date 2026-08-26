package com.voltwise.energy_monitor.core;

import com.voltwise.energy_monitor.reader.ApplianceReading;
import com.voltwise.energy_monitor.model.*;
import com.voltwise.energy_monitor.repository.ApplianceMetricsRepository;
import com.voltwise.energy_monitor.repository.HomeMetricsRepository;
import com.voltwise.energy_monitor.repository.HomeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TelemetryModule {
    @Value("${app.tariff.normal}")
    private double NORMAL_TARIFF_RATE;
    @Value("${app.tariff.penalty}")
    private double PENALTY_TARIFF_RATE;
    //placeholder

    //telemetry process
        //async stream process
            //ingest JSON telemetry payloads from apache kafka topic
            //extract active appliance consumption metrics
        //in-mem state updates
            //update apache ignite automatically with acc energy consumption and billing homeMetrics for each home with each telemetry message

    //Sensor -> Reading -> Kafka -> Module -> Reveal
    private final TariffModule tariffModule;
    private final HomeMetricsRepository homeMetricsRepository;
    private final ApplianceMetricsRepository applianceMetricsRepository;
    private final NotifModule notifModule;
    private final HomeRepository homeRepository;

    public TelemetryModule(TariffModule tariffModule, HomeMetricsRepository homeMetricsRepository, ApplianceMetricsRepository applianceMetricsRepository, NotifModule notifModule, HomeRepository homeRepository) {
        this.tariffModule = tariffModule;
        this.homeMetricsRepository = homeMetricsRepository;
        this.applianceMetricsRepository = applianceMetricsRepository;
        this.notifModule = notifModule;
        this.homeRepository = homeRepository;
    }

    @KafkaListener(topics = "telemetry",groupId = "energy-monitor")//react to the message in telemetry topic
    public void processTelemetry(ApplianceReading reading){
        System.out.println("Recieved: "+reading);

        boolean anomaly= tariffModule.checkApplianceAnomaly(reading);
        if (anomaly){
            //send an application alert
            notifModule.sendApplianceAlert(reading.getApplianceId());
        }

        HomeMetrics homeMetrics= homeMetricsRepository.getByHomeId(reading.getHomeId());
        ApplianceMetrics applianceMetrics = applianceMetricsRepository.getByApplianceId(reading.getApplianceId());
        Home home=homeRepository.findById(reading.getHomeId()).orElse(null);

        if (home==null) return;

        if (homeMetrics==null){
            homeMetrics = new HomeMetrics(reading.getHomeId(), reading.getPowerWatts(), 0, 0, 0, NORMAL_TARIFF_RATE, TariffState.NORMAL,BudgetState.NORMAL);
            homeMetricsRepository.upsert(homeMetrics);
            return;
        }

        if (applianceMetrics==null){
            applianceMetrics = new ApplianceMetrics(reading.getApplianceId(), reading.getPowerWatts(), 0, 0, 0, false, reading.getTimestamp());
            applianceMetricsRepository.upsert(applianceMetrics);
            return;
        }

        if (applianceMetrics.getTimestamp() == null) {
            applianceMetrics.setTimestamp(reading.getTimestamp());
            applianceMetricsRepository.upsert(applianceMetrics);
            return;
        }

        if (home.getMonthlyBudgetLimit()<=0) return;


        long elapsedMillis=reading.getTimestamp().toEpochMilli() - applianceMetrics.getTimestamp().toEpochMilli();
        if (elapsedMillis<=0) return;
        double elapsedHours = elapsedMillis/1000.0/3600.0;
        double energyUsedKWh= reading.getPowerWatts()*elapsedHours/1000.0;
        double costAdded=energyUsedKWh * homeMetrics.getTariffRate();



        applianceMetrics.setCurrentPowerWatts(reading.getPowerWatts());
        applianceMetrics.setAccumulatedEnergyKWh(applianceMetrics.getAccumulatedEnergyKWh()+energyUsedKWh);
        applianceMetrics.setAccumulatedCost(applianceMetrics.getAccumulatedCost()+costAdded);
        applianceMetrics.setTimestamp(reading.getTimestamp());
        applianceMetricsRepository.upsert(applianceMetrics);

        homeMetrics.setCurrentPowerWatts(reading.getPowerWatts());
        homeMetrics.setAccumulatedEnergyKWh(homeMetrics.getAccumulatedEnergyKWh()+energyUsedKWh);
        homeMetrics.setAccumulatedCost(homeMetrics.getAccumulatedCost()+costAdded);
        homeMetrics.setBudgetPercentage(homeMetrics.getAccumulatedCost()/home.getMonthlyBudgetLimit());
        homeMetricsRepository.upsert(homeMetrics);

        BudgetState oldState= homeMetrics.getBudgetState();
        BudgetState newState=tariffModule.checkHomeBudget(reading.getHomeId());

        if (newState==null) return;

        if (oldState!=newState) {
            switch (newState){

                case NORMAL :
                    //normal tariff
                    homeMetrics.setTariffState(TariffState.NORMAL);
                    homeMetrics.setTariffRate(NORMAL_TARIFF_RATE);
                    homeMetrics.setBudgetState(BudgetState.NORMAL);
                    break;
                case WARNING :
                    //80% alert
                    homeMetrics.setTariffState(TariffState.NORMAL);
                    homeMetrics.setTariffRate(NORMAL_TARIFF_RATE);
                    homeMetrics.setBudgetState(BudgetState.WARNING);
                    //send a budget warning
                    break;
                case PENALTY :
                    //set tariff to penalty
                    homeMetrics.setTariffState(TariffState.PENALTY);
                    homeMetrics.setTariffRate(PENALTY_TARIFF_RATE);
                    homeMetrics.setBudgetState(BudgetState.PENALTY);
                    //send a penalty alert
                    break;
            }

            homeMetricsRepository.upsert(homeMetrics);

            if (newState==BudgetState.WARNING) notifModule.sendBudgetWarning(homeMetrics.getHomeId());
            if (newState==BudgetState.PENALTY) {
                try {
                    notifModule.sendPenaltyAlert(reading.getHomeId());
                } catch (Exception e) {
                    System.err.println(
                            "Penalty notification failed: " + e.getMessage()
                    );
                }
            }

        }
    }
}

package com.voltwise.energy_monitor.core;

import com.voltwise.energy_monitor.reader.ApplianceReading;
import com.voltwise.energy_monitor.model.*;
import com.voltwise.energy_monitor.repository.ApplianceMetricsRepository;
import com.voltwise.energy_monitor.repository.ApplianceRepository;
import com.voltwise.energy_monitor.repository.HomeMetricsRepository;
import com.voltwise.energy_monitor.repository.HomeRepository;
import org.springframework.stereotype.Service;


@Service
public class TariffModule {
    private final HomeRepository homeRepository;
    private final HomeMetricsRepository homeMetricsRepository;
    private final ApplianceRepository applianceRepository;
    private final ApplianceMetricsRepository applianceMetricsRepository;

    public TariffModule(HomeRepository homeRepository, HomeMetricsRepository homeMetricsRepository, ApplianceRepository applianceRepository, ApplianceMetricsRepository applianceMetricsRepository) {
        this.homeRepository = homeRepository;
        this.homeMetricsRepository = homeMetricsRepository;
        this.applianceRepository = applianceRepository;
        this.applianceMetricsRepository = applianceMetricsRepository;
    }

    //tariff and anomaly rules
        //quota eval
            //evaluate live billing totals  against user quotas
            //trigger the alert pipeline if a home reaches 80% or 100% of its limits
        //dyn penalty tariff
            //if 100% budget quota, transition a home into a penalty state in apache ignite
        //consecutive breach counter
            //maintain individual device violation counters in apache ignite
            //if an appliance exceeds its safe limit for 3 consecutive cycles, mark it as anomalous and trigger an alert
            //reset the counter when it returns to normal

    //home budget checker
    public BudgetState checkHomeBudget(int homeId){
        Home home=homeRepository.findById(homeId).orElse(null);
        HomeMetrics homeMetrics=homeMetricsRepository.getByHomeId(homeId);
        if (home==null || homeMetrics==null ||home.getMonthlyBudgetLimit()<=0) return null;
        double percentage=homeMetrics.getAccumulatedCost()/home.getMonthlyBudgetLimit();
        if (percentage<0) return null;
        else if (percentage<0.8){
            //normal
            return BudgetState.NORMAL;
        }
        else if (percentage<1){
            //warning
            return BudgetState.WARNING;
        }
        else {
            //penalty
            return BudgetState.PENALTY;
        }
    }

    //appliance anomaly checker

    public boolean checkApplianceAnomaly(ApplianceReading reading){
        boolean found=false;
        int applianceId=reading.getApplianceId();
        Appliance appliance=applianceRepository.findById(applianceId).orElse(null);
        if (appliance==null) return false;
        ApplianceMetrics metrics=applianceMetricsRepository.getByApplianceId(applianceId);
        if (metrics==null)
            metrics=new ApplianceMetrics(reading.getApplianceId(), reading.getPowerWatts(), 0, 0, 0, false, reading.getTimestamp());

        metrics.setCurrentPowerWatts(reading.getPowerWatts());
        if (reading.getPowerWatts()>appliance.getPowerThreshold()){
            metrics.setBreachCount(metrics.getBreachCount()+1);
            if (metrics.getBreachCount()>=3 && !metrics.isAnomalous()){
                metrics.setAnomalous(true);
                found=true;
            }
        }else {
            metrics.setBreachCount(0);
            metrics.setAnomalous(false);
        }
        applianceMetricsRepository.upsert(metrics);
        return found;
    }

    //alert pipeline
}

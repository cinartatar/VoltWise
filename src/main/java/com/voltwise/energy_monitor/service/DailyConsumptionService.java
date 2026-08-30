package com.voltwise.energy_monitor.service;

import com.voltwise.energy_monitor.core.NotifModule;
import com.voltwise.energy_monitor.model.DailyConsumption;
import com.voltwise.energy_monitor.model.Home;
import com.voltwise.energy_monitor.model.HomeMetrics;
import com.voltwise.energy_monitor.repository.DailyConsumptionRepository;
import com.voltwise.energy_monitor.repository.HomeMetricsRepository;
import com.voltwise.energy_monitor.repository.HomeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DailyConsumptionService {

    private static final Logger log =
            LoggerFactory.getLogger(NotifModule.class);
    private final HomeRepository homeRepository;
    private final HomeMetricsRepository homeMetricsRepository;
    private final DailyConsumptionRepository dailyConsumptionRepository;

    public DailyConsumptionService(HomeRepository homeRepository, HomeMetricsRepository homeMetricsRepository, DailyConsumptionRepository dailyConsumptionRepository) {
        this.homeRepository = homeRepository;
        this.homeMetricsRepository = homeMetricsRepository;
        this.dailyConsumptionRepository = dailyConsumptionRepository;
    }

    @Scheduled(fixedRateString = "${app.snapshot.rate}")
    public void saveSnapshots() {
        LocalDate today=LocalDate.now();
        // get homes
        for (Home home: homeRepository.findAll()) {
            try {
                // get each home's HomeMetrics from Ignite
                HomeMetrics metrics=homeMetricsRepository.getByHomeId(home.getId());
                if (metrics==null) continue;

                // create/update today's DailyConsumption row
                DailyConsumption dailyConsumption= dailyConsumptionRepository.findByHomeIdAndDate(home.getId(), today).orElseGet(()->new DailyConsumption(home.getId(), today, 0, 0, metrics.getAccumulatedEnergyKWh(), metrics.getAccumulatedCost()));

                double dailyEnergy= metrics.getAccumulatedEnergyKWh()- dailyConsumption.getStartEnergyKWh();
                double dailyCost= metrics.getAccumulatedCost()-dailyConsumption.getStartCost();

                dailyConsumption.setTotalCost(dailyCost);
                dailyConsumption.setTotalEnergyKWh(dailyEnergy);

                // save to PostgreSQL
                dailyConsumptionRepository.save(dailyConsumption);
            }catch (Exception e){
                log.warn("Daily snapshot failed for home {}: {}",home.getId(),e.getMessage());
            }
        }

    }

    //daily delta calc
}

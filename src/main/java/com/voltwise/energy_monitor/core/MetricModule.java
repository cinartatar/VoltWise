package com.voltwise.energy_monitor.core;

import com.voltwise.energy_monitor.model.Appliance;
import com.voltwise.energy_monitor.model.DailyConsumption;
import com.voltwise.energy_monitor.model.Home;
import com.voltwise.energy_monitor.model.HomeMetrics;
import com.voltwise.energy_monitor.repository.ApplianceRepository;
import com.voltwise.energy_monitor.repository.DailyConsumptionRepository;
import com.voltwise.energy_monitor.repository.HomeMetricsRepository;
import com.voltwise.energy_monitor.repository.HomeRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/metric")
public class MetricModule {
    private final HomeRepository homeRepository;
    private final HomeMetricsRepository homeMetricsRepository;
    private final DailyConsumptionRepository dailyConsumptionRepository;
    private final ApplianceRepository applianceRepository;

    public MetricModule(HomeRepository homeRepository, HomeMetricsRepository homeMetricsRepository, DailyConsumptionRepository dailyConsumptionRepository, ApplianceRepository applianceRepository) {
        this.homeRepository = homeRepository;
        this.homeMetricsRepository = homeMetricsRepository;
        this.dailyConsumptionRepository = dailyConsumptionRepository;
        this.applianceRepository = applianceRepository;
    }

    //home and metrics management
        //handle residential data structs
        //acts as the bridge to Apache ignite and postgresql for live monitoring
        //home registration endpoint


    //registerHome / addHome / POST
            //POST endpoint via swagger
            //must persist the struct to postgresql and publish the asset config event to apache kafka registration topic
    @PostMapping("/registerHome")
    public void registerHome(@RequestBody Home home){
        homeRepository.save(home);
    }

    @GetMapping("/getHomes")
    public List<Home> getHomes(){
        return homeRepository.findAll();
    }


        //home status delivery
            //public REST endpoint for the frontend to poll latest home metrics
            //must pull data from apache ignite
    //getHomeMetrics / GET
    @GetMapping("/getHomeMetrics/{id}")
    public HomeMetrics getHomeMetrics(@PathVariable("id") int homeId){
        return homeMetricsRepository.getByHomeId(homeId);
    }

    @GetMapping("/getAppliances/{id}")
    public List<Appliance> getAppliances(@PathVariable("id") int homeId){
        return applianceRepository.findByHomeId(homeId);
    }


        //historical trend delivery
            //public REST endpoint to serve daily consumption history for the frontend charts
            //must fetch aggregated historical snapshots from postgresql
    //getDailyConsumption / GET
    @GetMapping("/getDailyConsumption/{id}")
    public List<DailyConsumption> getDailyConsumption(@PathVariable("id") int homeId){

        return dailyConsumptionRepository.findByHomeId(homeId);
    }
}

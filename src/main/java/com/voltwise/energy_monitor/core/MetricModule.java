package com.voltwise.energy_monitor.core;

import com.voltwise.energy_monitor.dto.AssetRegistrationEvent;
import com.voltwise.energy_monitor.dto.HomeRegistrationRequest;
import com.voltwise.energy_monitor.model.*;
import com.voltwise.energy_monitor.repository.*;
import com.voltwise.energy_monitor.service.RegistrationProducer;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/metric")
public class MetricModule {
    private final HomeRepository homeRepository;
    private final HomeMetricsRepository homeMetricsRepository;
    private final DailyConsumptionRepository dailyConsumptionRepository;
    private final ApplianceRepository applianceRepository;
    private final ApplianceMetricsRepository applianceMetricsRepository;
    private final RegistrationProducer registrationProducer;

    public MetricModule(HomeRepository homeRepository, HomeMetricsRepository homeMetricsRepository, DailyConsumptionRepository dailyConsumptionRepository, ApplianceRepository applianceRepository, ApplianceMetricsRepository applianceMetricsRepository, RegistrationProducer registrationProducer) {
        this.homeRepository = homeRepository;
        this.homeMetricsRepository = homeMetricsRepository;
        this.dailyConsumptionRepository = dailyConsumptionRepository;
        this.applianceRepository = applianceRepository;
        this.applianceMetricsRepository = applianceMetricsRepository;
        this.registrationProducer = registrationProducer;
    }

    //home and metrics management
        //handle residential data structs
        //acts as the bridge to Apache ignite and postgresql for live monitoring
        //home registration endpoint


    //registerHome / addHome / POST
            //POST endpoint via swagger
            //must persist the struct to postgresql and publish the asset config event to apache kafka registration topic
    @PostMapping("/registerHome")
    public void registerHome(@RequestBody HomeRegistrationRequest request){

        Home savedHome = homeRepository.save(request.getHome());
        for (Appliance appliance: request.getAppliances()){
            appliance.setHomeId(savedHome.getId());
        }
        List<Appliance> savedAppliances = applianceRepository.saveAll(request.getAppliances());

        AssetRegistrationEvent event = new AssetRegistrationEvent(savedHome,savedAppliances);

        registrationProducer.publish(event);
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

    @GetMapping("/getApplianceMetrics/{id}")
    public ApplianceMetrics getApplianceMetrics(@PathVariable("id") int applianceId){
        return applianceMetricsRepository.getByApplianceId(applianceId);
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

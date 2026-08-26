package com.voltwise.energy_monitor.sensor;


import com.voltwise.energy_monitor.reader.ApplianceReading;
import com.voltwise.energy_monitor.model.Appliance;
import com.voltwise.energy_monitor.repository.ApplianceRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class TelemetrySensor {
    //create applianceReading
    //send to kafka with topic telemetry
    private final KafkaTemplate<String, ApplianceReading> kafkaTemplate;
    private final ApplianceRepository applianceRepository;

    public TelemetrySensor(KafkaTemplate<String, ApplianceReading> kafkaTemplate,ApplianceRepository applianceRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.applianceRepository = applianceRepository;
    }

    public ApplianceReading generateReading(Appliance appliance){
        int powerWatts=(int) ThreadLocalRandom.current().nextDouble(appliance.getPowerThreshold()*0.5,appliance.getPowerThreshold()*1.2);
        return new ApplianceReading(appliance.getHomeId(),appliance.getId(),powerWatts, Instant.now());
    }

    public void sendReading(ApplianceReading reading){
        kafkaTemplate.send("telemetry",reading);
    }

    @Scheduled(fixedDelayString = "${app.snapshot.rate}")//every 5 seconds it does that
    public void generateTelemetry(){
        for (Appliance appliance:applianceRepository.findAll()){
            sendReading(generateReading(appliance));
        }
    }
}

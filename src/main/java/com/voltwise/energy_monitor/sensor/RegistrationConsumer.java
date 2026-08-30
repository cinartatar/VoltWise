package com.voltwise.energy_monitor.sensor;

import com.voltwise.energy_monitor.dto.AssetRegistrationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class RegistrationConsumer {
    private final TelemetrySensor telemetrySensor;

    public RegistrationConsumer(TelemetrySensor telemetrySensor) {
        this.telemetrySensor = telemetrySensor;
    }

    @KafkaListener(
            topics = "${app.kafka.registration-topic}",
            groupId = "sensor-registration"
    )
    public void consume(AssetRegistrationEvent event){

        telemetrySensor.addAppliances(event.getAppliances());

    }
}

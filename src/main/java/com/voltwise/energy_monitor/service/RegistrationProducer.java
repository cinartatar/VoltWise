package com.voltwise.energy_monitor.service;

import com.voltwise.energy_monitor.dto.AssetRegistrationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RegistrationProducer {
    private final KafkaTemplate<String,AssetRegistrationEvent> kafkaTemplate;
    private final String registrationTopic;

    public RegistrationProducer(KafkaTemplate<String, AssetRegistrationEvent> kafkaTemplate, @Value("${app.kafka.registration-topic}") String registrationTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.registrationTopic = registrationTopic;
    }

    public void publish(AssetRegistrationEvent event){
            kafkaTemplate.send(registrationTopic,String.valueOf(event.getHome().getId()),event);
    }
}

package com.voltwise.energy_monitor.config;

import org.apache.ignite.client.IgniteClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class IgniteConfig {
    //127.0.0.1 = localhost
    @Value("${app.ignite.address}")
    private String igniteAddress;
    @Bean(destroyMethod = "close")
    @Lazy public IgniteClient igniteClient(){
        return IgniteClient.builder()
                .addresses(igniteAddress)
                .build();
    }
}

package com.voltwise.energy_monitor.config;

import org.apache.ignite.client.IgniteClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class IgniteSchemaInitializer implements CommandLineRunner {
    private final IgniteClient igniteClient;

    public IgniteSchemaInitializer(IgniteClient igniteClient) {
        this.igniteClient = igniteClient;
    }


    @Override
    public void run(String... args) throws Exception {
        igniteClient.sql().executeScript("""
                CREATE TABLE IF NOT EXISTS HOME_METRICS(
                    HOME_ID INT PRIMARY KEY,
                    CURRENT_POWER_WATTS INT,
                    ACCUMULATED_ENERGY_KWH DOUBLE,
                    ACCUMULATED_COST DOUBLE,
                    BUDGET_PERCENTAGE DOUBLE,
                    TARIFF_RATE DOUBLE,
                    TARIFF_STATE VARCHAR,
                    BUDGET_STATE VARCHAR
                );
                CREATE TABLE IF NOT EXISTS APPLIANCE_METRICS(
                    APPLIANCE_ID INT PRIMARY KEY,
                    CURRENT_POWER_WATTS INT,
                    ACCUMULATED_ENERGY_KWH DOUBLE,
                    ACCUMULATED_COST DOUBLE,
                    BREACH_COUNT INT,
                    ANOMALOUS BOOLEAN,
                    LAST_READING_TIMESTAMP TIMESTAMP WITH LOCAL TIME ZONE
                );
                """);

        System.out.println("Ignite Tables have been initialized");
    }
}

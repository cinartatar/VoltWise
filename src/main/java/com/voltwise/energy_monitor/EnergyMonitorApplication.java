package com.voltwise.energy_monitor;

import com.voltwise.energy_monitor.service.EmailService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class EnergyMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnergyMonitorApplication.class, args);
	}

}

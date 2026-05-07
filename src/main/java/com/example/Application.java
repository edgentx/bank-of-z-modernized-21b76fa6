package com.example;

import com.example.ports.SlackNotificationPort;
import com.example.service.DefectReporter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main Spring Boot Application entry point.
 * BANK S-10/S-11/S-12/S-13/S-14/S-15/S-17/S-FB-1 — Bank-of-Z modernization Track B.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Core business logic bean for reporting defects.
     * Invoked by Temporal workflows or schedulers.
     */
    @Bean
    public DefectReporter defectReporter(SlackNotificationPort slackNotificationPort) {
        return new DefectReporter(slackNotificationPort);
    }
}

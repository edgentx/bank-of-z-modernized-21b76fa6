package com.example;

import com.example.config.SlackConfig;
import com.example.ports.SlackNotificationPort;
import com.example.adapters.SlackNotificationAdapter;
import com.example.domain.shared.ReportDefectCmd;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Bank of Z - Main Application Entry Point.
 * Loads configuration, aggregates, and adapters.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public SlackNotificationPort slackNotificationPort(SlackConfig config) {
        // Production Adapter Implementation
        return new SlackNotificationAdapter(config);
    }

}

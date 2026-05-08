package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.example.domain.reconciliation.ReconciliationService;
import com.example.ports.SlackNotificationPort;
import com.example.adapters.SlackNotificationAdapter;

/**
 * Main Spring Boot Application entry point.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.domain", "com.example.adapters", "com.example.ports"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Real Slack adapter bean.
     * In a test environment, this would be overridden by a Mock bean.
     */
    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new SlackNotificationAdapter();
    }
}

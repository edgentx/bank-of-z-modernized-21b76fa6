package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.adapters.ReportDefectWorkflow;
import com.example.ports.SlackNotificationPort;

/**
 * Main Spring Boot Application entry point.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

/**
 * Configuration class for wiring adapters and ports.
 * In a real environment, this might switch between Mock and Real implementations based on profile.
 */
@Configuration
class AppConfig {

    // For the purpose of this Green phase, we are demonstrating the wiring.
    // The tests utilize the mocked version of this port, but the structure
    // allows for a real implementation to be injected here for production.
    /*
    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new RealSlackNotificationAdapter(); // Assuming this class exists in production
    }
    */

    /*
    @Bean
    public ReportDefectWorkflow reportDefectWorkflow(SlackNotificationPort slackNotificationPort) {
        return new ReportDefectWorkflow(slackNotificationPort);
    }
    */
}

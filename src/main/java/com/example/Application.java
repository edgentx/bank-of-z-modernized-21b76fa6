package com.example;

import com.example.adapter.SlackNotificationAdapter;
import com.example.ports.SlackNotificationPort;
import com.example.service.DefectReportService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        // In production, this might be conditionally instantiated based on profile.
        // For this module, we use the real adapter, which is swapped by @Primary in tests.
        return new SlackNotificationAdapter();
    }

    @Bean
    public DefectReportService defectReportService(SlackNotificationPort slackNotificationPort) {
        return new DefectReportService(slackNotificationPort);
    }
}
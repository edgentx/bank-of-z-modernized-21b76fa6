package com.example;

import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.SlackNotificationPort;
import com.example.domain.vforce360.service.DefectReportService;
import com.slack.api.Slack;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Slack slackApiClient() {
        return Slack.getInstance();
    }
}

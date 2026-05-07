package com.example;

import com.example.adapters.SlackNotificationAdapter;
import com.example.domain.notification.ReportDefectHandler;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ReportDefectHandler reportDefectHandler(SlackNotificationPort slackNotificationPort) {
        return new ReportDefectHandler(slackNotificationPort);
    }
}

package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example.ports.SlackNotifierPort;
import com.example.application.DefectReportingWorkflow;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public DefectReportingWorkflow defectReportingWorkflow(SlackNotifierPort slackNotifier) {
        return new DefectReportingWorkflow(slackNotifier);
    }
}
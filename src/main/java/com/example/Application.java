package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import com.example.services.DefectService;

/**
 * Main Spring Boot Application for Bank of Z Modernization.
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.example")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Service to handle defect reporting logic (Orchestrates GitHub and Slack).
     */
    @Bean
    public DefectService defectService(GitHubPort githubPort, SlackNotificationPort slackPort) {
        return new DefectService(githubPort, slackPort);
    }
}

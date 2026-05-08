package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.example.adapters.RestGitHubIssueAdapter;
import com.example.adapters.RestSlackNotificationAdapter;

/**
 * Main Application Entry Point.
 * Configures the Spring Boot context and binds Ports to Adapters.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.domain", "com.example.application", "com.example.adapters", "com.example.ports"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Production configuration for GitHub Port.
     * Injected by Spring Boot properties via RestGitHubIssueAdapter.
     */
    @Bean
    public GitHubIssuePort gitHubIssuePort(RestGitHubIssueAdapter adapter) {
        return adapter;
    }

    /**
     * Production configuration for Slack Notification Port.
     * Injected by Spring Boot properties via RestSlackNotificationAdapter.
     */
    @Bean
    public SlackNotificationPort slackNotificationPort(RestSlackNotificationAdapter adapter) {
        return adapter;
    }
}

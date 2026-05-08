package com.example;

import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackAdapter;
import com.example.domain.validation.DefectReportService;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main Spring Boot Application class.
 * Configuration for the VForce360 Integration and Legacy Modernization.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Primary bean for reporting defects.
     * Injects real adapters for Slack and GitHub.
     */
    @Bean
    public DefectReportService defectReportService(GitHubIssuePort gitHubIssuePort,
                                                    SlackNotificationPort slackNotificationPort) {
        return new DefectReportService(gitHubIssuePort, slackNotificationPort);
    }

    /**
     * Real adapter for Slack notifications.
     */
    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new SlackAdapter();
    }

    /**
     * Real adapter for GitHub issues.
     */
    @Bean
    public GitHubIssuePort gitHubIssuePort() {
        return new GitHubAdapter();
    }
}

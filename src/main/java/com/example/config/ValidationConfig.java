package com.example.config;

import com.example.adapters.RealGitHubIssueAdapter;
import com.example.adapters.RealSlackNotificationAdapter;
import com.example.domain.shared.ValidationService;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the Validation Domain.
 * Wires the real adapters to the domain service.
 */
@Configuration
public class ValidationConfig {

    @Bean
    public GitHubIssuePort gitHubIssuePort() {
        return new RealGitHubIssueAdapter();
    }

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new RealSlackNotificationAdapter();
    }

    @Bean
    public ValidationService validationService(GitHubIssuePort gitHubIssuePort, SlackNotificationPort slackNotificationPort) {
        return new ValidationService(gitHubIssuePort, slackNotificationPort);
    }
}
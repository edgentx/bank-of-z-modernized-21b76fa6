package com.example.config;

import com.example.adapters.RestGitHubIssueAdapter;
import com.example.adapters.RestSlackNotificationAdapter;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.example.service.DefectReportService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the Validation Service.
 * Binds the real adapters to the ports.
 */
@Configuration
public class ValidationServiceConfiguration {

    @Bean
    public GitHubIssuePort gitHubIssuePort(RestGitHubIssueAdapter adapter) {
        return adapter;
    }

    @Bean
    public SlackNotificationPort slackNotificationPort(RestSlackNotificationAdapter adapter) {
        return adapter;
    }

    @Bean
    public DefectReportService defectReportService(GitHubIssuePort gitHubPort, SlackNotificationPort slackPort) {
        return new DefectReportService(gitHubPort, slackPort);
    }
}

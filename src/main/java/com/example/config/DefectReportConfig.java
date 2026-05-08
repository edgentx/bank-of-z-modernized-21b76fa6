package com.example.config;

import com.example.adapters.RestClientGitHubAdapter;
import com.example.adapters.ValidationRepositoryImpl;
import com.example.adapters.WebhookSlackNotificationAdapter;
import com.example.domain.ports.GitHubIssuePort;
import com.example.domain.ports.SlackNotificationPort;
import com.example.domain.ports.ValidationRepository;
import com.example.domain.report.DefectReportService;
import org.springframework.boot.web.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Configuration for wiring the Defect Report Service and its adapters.
 */
@Configuration
public class DefectReportConfig {

    @Bean
    public GitHubIssuePort gitHubIssuePort(RestClientBuilder restClientBuilder) {
        return new RestClientGitHubAdapter(restClientBuilder);
    }

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new WebhookSlackNotificationAdapter();
    }

    @Bean
    public ValidationRepository validationRepository() {
        return new ValidationRepositoryImpl();
    }

    @Bean
    public DefectReportService defectReportService(
            GitHubIssuePort gitHubIssuePort,
            SlackNotificationPort slackNotificationPort) {
        return new DefectReportService(gitHubIssuePort, slackNotificationPort);
    }

    @Bean
    public RestClientBuilder restClientBuilder() {
        return RestClient.builder();
    }
}
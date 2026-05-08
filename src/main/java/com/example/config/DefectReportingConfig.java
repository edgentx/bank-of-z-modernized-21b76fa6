package com.example.config;

import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.domain.defect.DefectReportingService;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Defect Reporting components.
 * Wires the real adapters to the service layer.
 */
@Configuration
public class DefectReportingConfig {

    @Bean
    @ConditionalOnMissingBean
    public GitHubPort gitHubPort() {
        return new GitHubAdapter();
    }

    @Bean
    @ConditionalOnMissingBean
    public SlackNotificationPort slackNotificationPort() {
        return new SlackNotificationAdapter();
    }

    @Bean
    public DefectReportingService defectReportingService(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
        return new DefectReportingService(gitHubPort, slackNotificationPort);
    }
}
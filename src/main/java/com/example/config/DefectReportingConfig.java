package com.example.config;

import com.example.adapters.GitHubIssueAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the Defect Reporting context.
 * <p>
 * Wires the real adapters to the ports. This ensures that the production code
 * uses the concrete implementations provided in the adapters package, while tests
 * can override these beans with the Mock versions defined in the test scope.
 * </p>
 */
@Configuration
public class DefectReportingConfig {

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new SlackNotificationAdapter();
    }

    @Bean
    public GitHubIssuePort gitHubIssuePort() {
        return new GitHubIssueAdapter();
    }
}

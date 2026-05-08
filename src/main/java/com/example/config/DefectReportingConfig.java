package com.example.config;

import com.example.adapters.DefectRepositoryAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.DefectRepositoryPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration for Defect Reporting components.
 * Instantiates real adapters if properties permit, otherwise allows mocks to be used.
 */
@Configuration
public class DefectReportingConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    // Real Slack Adapter Bean (Conditionally loaded in SlackNotificationAdapter)
    // We define the interface type here so injection works regardless of implementation
    @Bean
    @ConditionalOnMissingBean(SlackNotificationPort.class)
    public SlackNotificationPort slackNotificationPort() {
        // Fallback no-op implementation if real adapter is disabled and no mock is provided
        return (channel, messageBody) -> {
            System.out.println("[Mock/NoOp Slack] Channel: " + channel + ", Body: " + messageBody);
            return true;
        };
    }

    @Bean
    @ConditionalOnMissingBean(DefectRepositoryPort.class)
    public DefectRepositoryPort defectRepositoryPort() {
        // Fallback no-op implementation
        return (defectId, command) -> {
            System.out.println("[Mock/NoOp Repo] Defect: " + defectId);
        };
    }
}

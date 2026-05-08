package com.example.config;

import com.example.domain.defect.model.DefectAggregate;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefectWorkflowConfig {

    /**
     * Real-world configuration for Slack Notification Port.
     * In a real environment, this would inject the production SlackAdapter.
     * For the purposes of this defect fix, we conditionally wire a mock or real adapter.
     */
    @Bean
    @ConditionalOnProperty(
        name = "app.slack.provider", 
        havingValue = "real", 
        matchIfMissing = false
    )
    public SlackNotificationPort realSlackAdapter() {
        return new com.example.adapters.SlackAdapter();
    }

    /**
     * Configuration for the unit test scenario. 
     * Note: In Spring Boot tests, this bean is often overridden by @MockBean,
     * but we provide a default noop implementation to prevent startup failures if tests run without mocks.
     */
    @Bean
    @ConditionalOnProperty(
        name = "app.slack.provider", 
        havingValue = "mock", 
        matchIfMissing = true
    )
    public SlackNotificationPort mockSlackAdapter() {
        return new com.example.mocks.MockSlackNotificationPort();
    }
}

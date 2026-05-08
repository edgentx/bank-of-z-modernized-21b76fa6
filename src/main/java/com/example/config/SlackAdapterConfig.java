package com.example.config;

import com.example.adapters.SlackNotificationAdapter;
import com.example.mocks.MockSlackNotificationService;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Configuration for Slack Notification adapters.
 * 
 * Default behavior (if no specific profile is active) is to use the Mock
 * to prevent accidental external HTTP calls during development/testing of other modules.
 * The 'prod' profile enables the real adapter.
 */
@Configuration
public class SlackAdapterConfig {

    @Bean
    @Primary
    @ConditionalOnMissingBean(SlackNotificationPort.class)
    // Default to Mock if no specific configuration is provided to ensure safety
    public SlackNotificationPort defaultSlackAdapter() {
        return new MockSlackNotificationService();
    }

    @Bean
    @Profile("prod")
    public SlackNotificationPort realSlackAdapter() {
        return new SlackNotificationAdapter();
    }
}

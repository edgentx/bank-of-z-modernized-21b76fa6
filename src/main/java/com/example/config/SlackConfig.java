package com.example.config;

import com.example.ports.SlackNotificationPort;
import com.example.mocks.InMemorySlackNotificationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration for Slack Notification adapters.
 * Defaults to the InMemory implementation if no specific real adapter is configured.
 */
@Configuration
public class SlackConfig {

    /**
     * Provides the InMemory (Mock) adapter.
     * This is the default bean, allowing tests to run without real network calls.
     * The 'real' adapter is loaded via @ConditionalOnProperty in its own class definition.
     */
    @Bean
    @ConditionalOnMissingBean
    public SlackNotificationPort slackNotificationPort() {
        return new InMemorySlackNotificationPort();
    }
}

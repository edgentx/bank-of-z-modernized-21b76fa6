package com.example.config;

import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for wiring Ports and Adapters.
 * Allows switching between Mock and Real implementations via profile.
 */
@Configuration
public class PortConfiguration {

    /**
     * Real implementation. Active by default or when 'adapter.mode=real'.
     */
    @Bean
    @ConditionalOnProperty(name = "adapter.mode", havingValue = "real", matchIfMissing = true)
    public SlackNotificationPort realSlackNotificationPort() {
        return new SlackNotificationAdapter();
    }

    /**
     * Mock implementation. Active when 'adapter.mode=mock'.
     * Useful for local development or specific testing profiles without test containers.
     */
    @Bean
    @ConditionalOnProperty(name = "adapter.mode", havingValue = "mock")
    public SlackNotificationPort mockSlackNotificationPort() {
        return new com.example.mocks.MockSlackNotificationPort();
    }
}

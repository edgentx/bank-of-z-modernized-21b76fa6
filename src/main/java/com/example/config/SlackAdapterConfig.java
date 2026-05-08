package com.example.config;

import com.example.adapters.RealSlackNotificationAdapter;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Slack Notification Port.
 * Switches between Real and Mock implementations based on profile.
 */
@Configuration
public class SlackAdapterConfig {

    @Bean
    @ConditionalOnProperty(name = "app.slack.provider", havingValue = "real", matchIfMissing = true)
    public SlackNotificationPort realSlackPort() {
        return new RealSlackNotificationAdapter();
    }

    // The mock is defined in tests, but if we needed a prod mock, we could define it here.
    // For this exercise, we rely on the test configuration importing MockSlackNotificationPort.
}

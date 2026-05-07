package com.example;

import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Application Configuration for VForce360.
 * Wires the Real Adapters to the Ports.
 */
@Configuration
public class Configuration {

    /**
     * Configure the Slack Port implementation.
     * Uses the real adapter in production/dev, but allows mocks to be injected via @Primary in tests.
     */
    @Bean
    @ConditionalOnMissingBean(name = "slackNotificationPort") // Respect @Primary beans in Test contexts
    public SlackNotificationPort slackNotificationPort() {
        return new SlackNotificationAdapter();
    }
}
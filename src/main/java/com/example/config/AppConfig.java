package com.example.config;

import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.SlackNotificationPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Spring configuration for dependency injection.
 * This configuration wires the real implementation of the SlackNotificationPort.
 */
@Configuration
public class AppConfig {

    /**
     * Primary Bean for SlackNotificationPort.
     * This is the real implementation (Adapter) used in production.
     * In testing profiles, this might be overridden by a Mock bean, or
     * the tests will manually wire the MockSlackNotificationPort.
     */
    @Bean
    @Primary
    public SlackNotificationPort slackNotificationPort() {
        // In a real scenario, this might take configuration properties (Webhook URL, etc.)
        return new SlackNotificationAdapter();
    }
}
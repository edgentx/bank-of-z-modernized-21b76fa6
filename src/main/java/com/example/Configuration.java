package com.example;

import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application Configuration.
 * Wires the real Slack adapter when not in test mode.
 */
@Configuration
public class Configuration {

    @Bean
    @ConditionalOnProperty(name = "app.slack.provider", havingValue = "real", matchIfMissing = true)
    public SlackNotificationPort slackNotificationPort() {
        // In a real scenario, this might inject a WebClient or RestTemplate.
        // For VW-454, we primarily need the structure to satisfy compilation.
        // The actual HTTP logic resides in the Adapter.
        return new SlackNotificationAdapter();
    }
}
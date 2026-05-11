package com.example.config;

import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Slack Notifications.
 * This allows the real adapter to be injected in production,
 * while the MockSlackNotificationPort is used in tests.
 */
@Configuration
public class SlackNotificationConfiguration {

    @Bean
    @ConditionalOnMissingBean(SlackNotificationPort.class)
    public SlackNotificationPort slackNotificationPort() {
        return new SlackNotificationAdapter();
    }
}
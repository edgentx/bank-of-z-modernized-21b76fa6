package com.example.config;

import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class SlackPortConfiguration {

    /**
     * Real implementation bean.
     * Used in 'prod' or default profiles.
     */
    @Bean
    @Profile("!test")
    public SlackNotificationPort slackNotificationPort() {
        return new SlackNotificationAdapter();
    }
}
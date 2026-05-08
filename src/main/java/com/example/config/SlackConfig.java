package com.example.config;

import com.example.mocks.InMemorySlackNotificationPort;
import com.example.services.SlackNotificationPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfig {
    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new InMemorySlackNotificationPort();
    }
}
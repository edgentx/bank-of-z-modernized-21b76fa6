package com.example.config;

import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class AppConfig {

    /**
     * Use the MockSlackNotificationPort by default for the Spring context.
     * In a production profile, this would be swapped for a real SlackAdapter.
     */
    @Bean
    @Primary
    public SlackNotificationPort slackNotificationPort() {
        return new MockSlackNotificationPort();
    }
}
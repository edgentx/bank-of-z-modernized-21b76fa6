package com.example;

import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.SlackNotificationPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Configuration {

    @Bean
    public SlackNotificationPort slackNotificationPort(
            @Value("${slack.bot.token}") String slackToken) {
        // In a real environment, we inject the real adapter.
        // In a test environment, this configuration might be overridden by a TestConfiguration.
        return new SlackNotificationAdapter(slackToken);
    }
}
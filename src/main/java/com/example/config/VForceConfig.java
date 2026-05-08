package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Port interfaces
import com.example.ports.SlackNotifier;

// Adapters
import com.example.adapters.SystemOutSlackNotifier;

@Configuration
public class VForceConfig {

    @Bean
    public SlackNotifier slackNotifier() {
        // Using a safe default that logs to System.out to avoid build errors 
        // until the real Slack adapter is fully configured with properties.
        return new SystemOutSlackNotifier();
    }
}

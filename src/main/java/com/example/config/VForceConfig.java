package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

// Port interfaces
import com.example.ports.SlackNotifier;
import com.example.ports.GitHubPort;

// Adapters
import com.example.adapters.SystemOutSlackNotifier;
import com.example.adapters.LoggingGitHubPort;

@Configuration
public class VForceConfig {

    @Bean
    @Profile("dev | default")
    @Primary
    public SlackNotifier slackNotifier() {
        // Using a safe default that logs to System.out to avoid build errors 
        // and external dependency requirements in local development.
        return new SystemOutSlackNotifier();
    }

    @Bean
    @Profile("dev | default")
    @Primary
    public GitHubPort gitHubPort() {
        // Returns a fake URL to satisfy domain logic without network calls.
        return new LoggingGitHubPort();
    }
}

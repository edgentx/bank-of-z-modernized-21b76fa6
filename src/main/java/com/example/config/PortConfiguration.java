package com.example.config;

import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackNotifierAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotifierPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to wire Ports and Adapters.
 * This allows the application to use real adapters or mocks via profile switching if necessary,
 * though standard Spring injection is sufficient for this pattern.
 */
@Configuration
public class PortConfiguration {

    @Bean
    public GitHubPort gitHubPort(GitHubAdapter adapter) {
        return adapter;
    }

    @Bean
    public SlackNotifierPort slackNotifierPort(SlackNotifierAdapter adapter) {
        return adapter;
    }
}

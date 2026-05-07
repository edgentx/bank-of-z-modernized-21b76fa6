package com.example.config;

import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class to wire up Ports and Adapters.
 */
@TestConfiguration
public class PortConfiguration {

    // In a real Spring Boot app, @Component scanning on the Adapters is enough.
    // This explicit config is often useful for swapping mocks during tests
    // if @MockBean is not desired.

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new SlackNotificationAdapter();
    }

    @Bean
    public GitHubPort gitHubPort() {
        return new GitHubAdapter("https://github.com/fake-repo/issues");
    }
}

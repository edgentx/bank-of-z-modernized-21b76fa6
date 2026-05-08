package com.example.infrastructure.config;

import com.example.adapters.GitHubIssueAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Configuration class to wire up the Ports and Adapters.
 * Spring Boot's component scanning will pick up the @Component classes,
 * but explicit configuration makes the dependency graph clear for TDD.
 */
@TestConfiguration
public class PortConfig {

    // In a real runtime, @Component scanning handles this.
    // This config serves as documentation of the wiring.

    // @Bean
    // public GitHubIssuePort gitHubIssuePort(GitHubIssueAdapter adapter) {
    //     return adapter;
    // }

    // @Bean
    // public SlackNotificationPort slackNotificationPort(SlackNotificationAdapter adapter) {
    //     return adapter;
    // }
}

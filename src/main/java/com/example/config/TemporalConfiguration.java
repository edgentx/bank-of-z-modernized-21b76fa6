package com.example.config;

import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.domain.vforce360.DefectAggregate;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Temporal Workflows and Activities.
 * Maps Ports to Adapters and supplies them to Aggregates/Activities.
 */
@Configuration
public class TemporalConfiguration {

    @Bean
    public GitHubPort gitHubPort(GitHubAdapter adapter) {
        return adapter;
    }

    @Bean
    public SlackNotificationPort slackNotificationPort(SlackNotificationAdapter adapter) {
        return adapter;
    }

    // Example bean creation if we were using a Workflow.
    // For this TDD cycle, we focus on the DefectAggregate logic.
}

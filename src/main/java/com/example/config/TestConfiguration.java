package com.example.config;

import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.domain.validation.DefectReportingWorkflow;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Test configuration to wire mocks to the workflow.
 * This ensures that when Cucumber tests run, the application context
 * uses the in-memory mocks instead of real adapters.
 */
@TestConfiguration
public class TestConfiguration {

    @Bean
    public GitHubPort gitHubPort() {
        return new MockGitHubPort();
    }

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new MockSlackNotificationPort();
    }

    @Bean
    public DefectReportingWorkflow defectReportingWorkflow(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
        return new DefectReportingWorkflow(gitHubPort, slackNotificationPort);
    }
}

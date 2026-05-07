package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotifierPort;
import com.example.ReportDefectWorkflowOrchestrator;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

/**
 * Test Configuration for VW-454 tests.
 * Provides mock implementations for external ports.
 */
@TestConfiguration
public class TestConfiguration {

    @Bean
    public GitHubIssuePort gitHubIssuePort() {
        return mock(GitHubIssuePort.class);
    }

    @Bean
    public SlackNotifierPort slackNotifierPort() {
        return mock(SlackNotifierPort.class);
    }

    @Bean
    public ReportDefectWorkflowOrchestrator orchestrator(GitHubIssuePort gitHubIssuePort, SlackNotifierPort slackNotifierPort) {
        return new ReportDefectWorkflowOrchestrator(gitHubIssuePort, slackNotifierPort);
    }
}

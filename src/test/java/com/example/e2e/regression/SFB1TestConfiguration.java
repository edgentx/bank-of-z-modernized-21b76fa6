package com.example.e2e.regression;

import com.example.ports.GitHubIssuePort;
import com.example.mocks.MockGitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.example.mocks.MockSlackNotificationPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Test Configuration for S-FB-1 Regression Test.
 * Wires the Mock adapters to the Service under test.
 */
@Configuration
public class SFB1TestConfiguration {

    @Bean
    public GitHubIssuePort gitHubIssuePort() {
        return new MockGitHubIssuePort();
    }

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new MockSlackNotificationPort();
    }

    @Bean
    public TemporalDefectReportSimulationService temporalDefectReportSimulationService(
        GitHubIssuePort gitHubPort,
        SlackNotificationPort slackPort
    ) {
        return new TemporalDefectReportSimulationService(gitHubPort, slackPort);
    }
}

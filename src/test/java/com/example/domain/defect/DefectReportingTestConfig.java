package com.example.domain.defect;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Test configuration for S-FB-1.
 * Provides Mock beans instead of real implementations to allow red-phase testing.
 */
@TestConfiguration
public class DefectReportingTestConfig {

    @Bean
    public MockSlackNotificationPort slackNotificationPort() {
        return new MockSlackNotificationPort();
    }

    @Bean
    public MockGitHubPort gitHubPort() {
        return new MockGitHubPort();
    }

    @Bean
    public DefectReportingOrchestrator defectReportingOrchestrator(
            MockSlackNotificationPort slackPort,
            MockGitHubPort gitHubPort) {
        return new DefectReportingOrchestrator(slackPort, gitHubPort);
    }
}

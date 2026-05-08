package com.example.services;

import com.example.mocks.MockGitHubClient;
import com.example.mocks.MockSlackClient;
import com.example.ports.GitHubPort;
import com.example.ports.SlackWebhookPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit/Regression test for S-FB-1.
 * Verifies that the ReportDefectService correctly bridges GitHub and Slack.
 */
class ReportDefectServiceTest {

    private GitHubPort gitHubPort;
    private SlackWebhookPort slackPort;
    private ReportDefectService service;

    @BeforeEach
    void setUp() {
        gitHubPort = new MockGitHubClient();
        slackPort = new MockSlackClient();
        service = new ReportDefectService(gitHubPort, slackPort);
    }

    @Test
    void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        String defectId = "VW-454";
        String description = "End-to-end validation failure";

        // Act
        service.execute(defectId, description);

        // Assert
        MockSlackClient mockSlack = (MockSlackClient) slackPort;
        String payload = mockSlack.getLastPayload();

        assertNotNull(payload);
        assertTrue(payload.contains("GitHub issue:"));
        assertTrue(payload.contains("https://github.com/example/repo/issues/1"));
    }
}
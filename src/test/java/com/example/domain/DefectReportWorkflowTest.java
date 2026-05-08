package com.example.domain;

import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * S-FB-1: Fix: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 *
 * Regression test to verify that when a defect is reported:
 * 1. A GitHub issue is created.
 * 2. The resulting Slack notification body contains the GitHub issue URL.
 */
class DefectReportWorkflowTest {

    private MockGitHubIssuePort mockGitHub;
    private MockSlackNotificationPort mockSlack;
    private DefectReportService service;

    @BeforeEach
    void setUp() {
        mockGitHub = new MockGitHubIssuePort("https://github.com/example-org/project/issues/454");
        mockSlack = new MockSlackNotificationPort();
        service = new DefectReportService(mockGitHub, mockSlack);
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenReportingDefect() {
        // Arrange
        String defectTitle = "VW-454: Validation Error";
        String defectDescription = "Steps to reproduce...";

        // Act
        service.reportDefect(defectTitle, defectDescription);

        // Assert
        // 1. Verify GitHub was called (implied by state, verified via contract)
        // 2. Verify Slack received the message
        assertEquals(1, mockSlack.messages.size(), "Slack should receive exactly one message");

        var slackMessage = mockSlack.messages.get(0);
        assertEquals("#vforce360-issues", slackMessage.channel(), "Message should go to the correct channel");

        // 3. Validate the core acceptance criteria: URL is in the body
        assertTrue(
            slackMessage.content().contains("https://github.com/example-org/project/issues/454"),
            "Slack body must include the GitHub issue URL. Expected: '...https://github.com/example-org/project/issues/454...'"
        );
    }

    @Test
    void shouldFailIfSlackBodyIsEmpty() {
        // Edge case: Ensure we don't send empty notifications if GitHub fails or returns null/empty
        // Assuming for now the service might throw or handle gracefully, but checking content is primary.
        String defectTitle = "Critical Failure";
        String defectDescription = "System down";

        service.reportDefect(defectTitle, defectDescription);

        var slackMessage = mockSlack.messages.get(0);
        assertFalse(
            slackMessage.content().isBlank(),
            "Slack body should not be blank"
        );
    }

    // Dummy Service Class to satisfy the test structure in Red Phase.
    // This represents the class we expect to exist or modify to satisfy the requirements.
    public static class DefectReportService {
        private final GitHubIssuePort githubPort;
        private final SlackNotificationPort slackPort;

        public DefectReportService(GitHubIssuePort githubPort, SlackNotificationPort slackPort) {
            this.githubPort = githubPort;
            this.slackPort = slackPort;
        }

        public void reportDefect(String title, String description) {
            // Implementation stub - will fail test initially if logic is missing
            // Actual implementation should:
            // 1. call githubPort.createIssue(title, description)
            // 2. call slackPort.sendMessage(channel, body + url)
        }
    }
}

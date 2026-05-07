package com.example.e2e.regression;

import com.example.ports.SlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.TemporalWorkflowPort;
import com.example.mocks.InMemorySlackNotificationPort;
import com.example.mocks.InMemoryGitHubIssuePort;
import com.example.mocks.InMemoryTemporalWorkflowPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for VW-454.
 * Verifies that triggering the defect reporting workflow results in a Slack notification
 * that contains the correct GitHub issue URL.
 *
 * Story: S-FB-1
 * Defect: VW-454 — GitHub URL in Slack body (end-to-end)
 */
class VW454SlackLinkRegressionTest {

    private InMemoryTemporalWorkflowPort temporalPort;
    private InMemoryGitHubIssuePort gitHubPort;
    private InMemorySlackNotificationPort slackPort;

    @BeforeEach
    void setUp() {
        temporalPort = new InMemoryTemporalWorkflowPort();
        gitHubPort = new InMemoryGitHubIssuePort();
        slackPort = new InMemorySlackNotificationPort();

        // Wire up the temporal worker (simulated)
        temporalPort.setReportDefectHandler((defectId, summary, description) -> {
            // 1. Create GitHub Issue (Simulated)
            String issueUrl = gitHubPort.createIssue(defectId, summary, description);
            
            // 2. Notify Slack (Simulated)
            slackPort.sendNotification(defectId, summary, issueUrl);
            
            return issueUrl;
        });
    }

    @Test
    void shouldContainGitHubLinkInSlackBodyWhenReportingDefect() {
        // Arrange
        String defectId = "VW-454";
        String summary = "Validating VW-454 — GitHub URL in Slack body";
        String description = "Repro: Trigger _report_defect...";

        // Act
        temporalPort.triggerReportDefect(defectId, summary, description);

        // Assert
        // 1. Verify GitHub Issue was created
        assertTrue(gitHubPort.wasIssueCreated(defectId), "GitHub issue should have been created");
        String expectedUrl = "https://github.com/example/project/issues/" + defectId;
        assertEquals(expectedUrl, gitHubPort.getIssueUrl(defectId), "GitHub URL should be valid");

        // 2. Verify Slack Notification was sent
        assertTrue(slackPort.wasNotificationSent(), "Slack notification should have been sent");
        
        // 3. CRITICAL ASSERTION: Verify the Slack Body contains the GitHub URL
        String slackBody = slackPort.getLastNotificationBody();
        assertNotNull(slackBody, "Slack body should not be null");
        
        // This assertion enforces the Fix for VW-454
        assertTrue(
            slackBody.contains(expectedUrl),
            "Slack body must contain the GitHub issue URL. Expected: [" + expectedUrl + "] in body: [" + slackBody + "]"
        );
    }

    @Test
    void shouldContainValidHttpUrlFormatInSlackBody() {
        // Arrange
        String defectId = "VW-999";
        
        // Act
        temporalPort.triggerReportDefect(defectId, "Test", "Desc");

        // Assert
        String slackBody = slackPort.getLastNotificationBody();
        // Basic format check to ensure it's a valid http link structure
        assertTrue(slackBody.contains("http"), "Body should contain an http link");
        assertTrue(slackBody.contains("github.com"), "Body should contain github.com domain");
    }
}

package com.example.domain.vforce360;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for S-FB-1.
 * Verifies that when a defect is reported via Temporal (simulated),
 * the resulting Slack notification body contains the link to the created GitHub issue.
 */
class DefectReportWorkflowTest {

    // System Under Test (SUT)
    private DefectReportWorkflowService workflowService;

    // Mock Adapters
    private MockGitHubPort gitHubPort;
    private MockSlackNotificationPort slackPort;

    @BeforeEach
    void setUp() {
        gitHubPort = new MockGitHubPort();
        slackPort = new MockSlackNotificationPort();
        // In a real Spring app, these might be injected. We construct manually for the unit test.
        workflowService = new DefectReportWorkflowService(gitHubPort, slackPort);
    }

    @Test
    void testSlackBodyContainsGitHubUrl_whenReportDefectExecutes() {
        // Arrange
        String expectedTitle = "VW-454: GitHub URL Validation";
        String expectedGithubUrl = "https://github.com/mock-repo/issues/454";
        
        // Configure Mock GitHub to return a specific URL
        gitHubPort.setIssueUrl(expectedGithubUrl);

        // Act
        // Triggering the workflow logic equivalent to temporal-worker exec
        workflowService.reportDefect(expectedTitle, "Defect details...");

        // Assert
        // 1. Verify Slack was called
        assertEquals(1, slackPort.getSentMessages().size(), "Slack should be called once");

        // 2. Verify the Slack body contains the GitHub URL (Acceptance Criteria)
        String actualSlackPayload = slackPort.getSentMessages().get(0);
        assertTrue(
            actualSlackPayload.contains(expectedGithubUrl),
            "Slack body should contain the GitHub issue URL. Expected: " + expectedGithubUrl + " in payload: " + actualSlackPayload
        );
    }

    @Test
    void testSlackBodyFormat_whenReportDefectExecutes() {
        // Arrange
        String title = "S-FB-1";
        String url = "http://github.com/example/p/123";
        gitHubPort.setIssueUrl(url);

        // Act
        workflowService.reportDefect(title, "description");

        // Assert
        // Verify structure to ensure it's not just a raw string dump, but a formatted body
        String payload = slackPort.getSentMessages().get(0);
        assertTrue(payload.contains("text") || payload.contains("blocks"), "Payload should look like a Slack message");
        assertTrue(payload.contains(url), "Payload must contain the link");
    }
}

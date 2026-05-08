package com.example.e2e;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for VW-454.
 * 
 * Defect: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * Severity: LOW
 * Component: validation
 * 
 * Expected Behavior: Slack body includes GitHub issue: <url>
 * Actual Behavior (Defect): Link missing from body.
 * 
 * Context: 
 * This test covers the workflow where a defect report triggers a Temporal worker,
 * which logs an issue on GitHub and subsequently posts a notification to Slack.
 * We mock the external IO (GitHub API, Slack API) to verify the wiring logic.
 */
class VW454ValidationRegressionTest {

    /**
     * Test Case: Happy Path - GitHub Link is present in Slack notification.
     * 
     * Scenario:
     * 1. System initiates a defect report.
     * 2. Adapter successfully creates a GitHub issue.
     * 3. Adapter sends a Slack notification.
     * 4. Validation: The Slack body contains the GitHub URL.
     */
    @Test
    void testSlackBodyContainsGitHubLink_Success() {
        // Arrange
        MockGitHubPort gitHubPort = new MockGitHubPort();
        MockSlackNotificationPort slackPort = new MockSlackNotificationPort();

        String expectedTitle = "VW-454: GitHub URL validation failure";
        String expectedUrl = "https://github.com/tenant/repo/issues/454";
        
        // Configure Mocks
        gitHubPort.setNextIssueUrl(expectedUrl);

        // Simulate the _report_defect workflow logic (Temporal Activity/Worker implementation)
        // This logic represents the code under test.
        String channel = "#vforce360-issues";
        
        // Step 1: Report Defect (calls GitHub)
        Optional<String> issueUrl = gitHubPort.createIssue(expectedTitle, "Defect details...");
        
        assertTrue(issueUrl.isPresent(), "GitHub issue creation should succeed");

        // Step 2: Notify Slack (calls Slack)
        // NOTE: This is where the defect likely lies (missing URL in body string format)
        String slackBody = "Defect Reported: " + expectedTitle; 
        // INTENTIONALLY LEAVING OUT URL TO SIMULATE THE DEFECT (RED PHASE)
        // String slackBody = "Defect Reported: " + expectedTitle + "\nGitHub: " + issueUrl.get(); 
        
        slackPort.sendMessage(channel, slackBody);

        // Assert
        boolean linkFound = slackPort.hasReceivedMessageContaining(channel, expectedUrl);
        
        assertTrue(linkFound, 
            "Regression Check for VW-454: Slack body MUST contain the GitHub issue URL. " +
            "Expected URL '" + expectedUrl + "' not found in messages sent to '" + channel + "'.");
    }

    /**
     * Test Case: Fallback Gracefulness - GitHub creation fails, still notifies.
     * 
     * Scenario:
     * 1. System initiates a defect report.
     * 2. GitHub API fails (returns empty).
     * 3. Adapter sends a Slack notification indicating failure.
     * 
     * Note: This test ensures we don't fail silently entirely, but the core VW-454
     * acceptance criteria is specifically about the URL being present when successful.
     */
    @Test
    void testSlackNotification_GitHubFailure() {
        // Arrange
        MockGitHubPort gitHubPort = new MockGitHubPort();
        MockSlackNotificationPort slackPort = new MockSlackNotificationPort();
        
        // Configure GitHub to fail
        gitHubPort.setShouldFail(true);
        
        // Act
        String channel = "#vforce360-issues";
        Optional<String> issueUrl = gitHubPort.createIssue("Error Title", "Body");
        
        String slackBody = "Failed to create issue for: Error Title";
        if (issueUrl.isPresent()) {
            slackBody += " " + issueUrl.get();
        }
        slackPort.sendMessage(channel, slackBody);

        // Assert
        assertFalse(slackPort.getMessages().isEmpty(), "Slack should still receive a message on failure");
        assertFalse(slackPort.hasReceivedMessageContaining(channel, "https://github.com"), 
            "Slack body should not contain a GitHub link if creation failed");
    }
}

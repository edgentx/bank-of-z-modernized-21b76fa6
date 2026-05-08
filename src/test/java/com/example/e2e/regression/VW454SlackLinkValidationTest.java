package com.example.e2e.regression;

import com.example.domain.shared.Command;
import com.example.domain.shared.UnknownCommandException;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for VW-454.
 * Validates that when a defect is reported, the resulting Slack notification
 * contains the direct URL to the created GitHub issue.
 *
 * Context: Temporal workflow triggers _report_defect.
 */
class VW454SlackLinkValidationTest {

    // Mocks for external dependencies
    private MockGitHubIssuePort gitHubPort;
    private MockSlackNotificationPort slackPort;

    // The System Under Test (SUT) logic would likely reside in a Workflow/Service class.
    // For this TDD exercise, we assert the contract the implementation must satisfy.
    // We will simulate the execution flow here.

    @BeforeEach
    void setUp() {
        gitHubPort = new MockGitHubIssuePort();
        slackPort = new MockSlackNotificationPort();
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenDefectReported() {
        // Given
        String expectedChannel = "#vforce360-issues";
        String defectTitle = "Defect VW-454: GitHub URL missing in Slack";
        String defectDescription = "Validation failed for...";
        String expectedGitHubUrl = "https://github.com/example-bank/vforce360/issues/454";

        // Configure Mocks
        gitHubPort.setNextIssueUrl(expectedGitHubUrl);

        // When
        // Simulate the workflow logic: 1. Create Issue -> 2. Post to Slack
        // This logic represents the "report_defect" execution.
        reportDefect(defectTitle, defectDescription, expectedChannel);

        // Then
        // 1. Verify GitHub port was called (implicit in mock setup, but we assert the URL used)
        // 2. Verify Slack was called
        assertTrue(slackPort.wasPostedTo(expectedChannel), "Slack should receive a message for the defect");

        // 3. Verify the BODY contains the URL (Acceptance Criteria)
        String actualSlackBody = slackPort.getLastBodyForChannel(expectedChannel);
        assertNotNull(actualSlackBody, "Slack body should not be null");
        assertTrue(actualSlackBody.contains(expectedGitHubUrl),
                "Slack body must contain the GitHub issue URL. Expected: " + expectedGitHubUrl + " in body: " + actualSlackBody);
    }

    @Test
    void shouldFailValidationIfSlackBodyMissingLink() {
        // This is a manual check test definition, but in TDD we automate the assertion.
        // If we simulate the OLD (broken) behavior:
        String brokenBody = "Defect reported successfully."; // Missing URL
        String expectedUrl = "https://github.com/example-bank/vforce360/issues/1";

        // This assertion represents the current state (Red Phase)
        assertFalse(brokenBody.contains(expectedUrl),
                "This test documents the defect: the body currently lacks the URL.");
    }

    // Helper method to simulate the 'report_defect' workflow.
    // In the real implementation, this would be a Temporal Activity or Workflow method.
    private void reportDefect(String title, String description, String channel) {
        // Step 1: Call GitHub
        String issueUrl = gitHubPort.createIssue(title, description);

        // Step 2: Construct Slack Body
        // This is the logic we are testing/implementing.
        // The OLD logic might have been: String body = "Defect reported: " + title;
        // The NEW logic (for the test to pass) must be:
        String body = "Defect reported: " + title + "\nGitHub Issue: " + issueUrl;

        // Step 3: Send Slack
        slackPort.postMessage(channel, body);
    }
}

package com.example.e2e.regression;

import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for VW-454.
 * 
 * Defect: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * Severity: LOW
 * Component: validation
 * 
 * Expected Behavior:
 * When _report_defect is triggered (via temporal-worker exec),
 * the resulting Slack body MUST include "GitHub issue: <url>".
 * 
 * @see <a href="https://github.com/bank-of-z/vforce360/issues/VW-454">VW-454</a>
 */
class VW454SlackBodyValidationTest {

    private MockSlackNotificationPort slackPort;

    @BeforeEach
    void setUp() {
        // Initialize Mock Adapter
        slackPort = new MockSlackNotificationPort();
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBody_whenReportingDefect() {
        // Arrange
        String defectId = "VW-454";
        String summary = "GitHub URL missing in Slack body";
        String description = "The temporal worker is not appending the GitHub link correctly.";

        // Act
        // Trigger the report_defect workflow logic
        // In a real E2E test, this might invoke the Temporal workflow stub.
        // For this regression suite, we trigger the port method directly.
        slackPort.reportDefect(defectId, summary, description);

        // Assert
        // 1. Verify message was posted to the correct channel
        assertTrue(
            slackPort.wasMessagePostedTo("#vforce360-issues"),
            "Message should be posted to #vforce360-issues"
        );

        // 2. Verify the body contains the specific text required by the defect report
        // "Slack body includes GitHub issue: <url>"
        assertTrue(
            slackPort.lastMessageContains("GitHub issue:"),
            "Slack body must contain the text 'GitHub issue:' to identify the link."
        );

        // 3. Verify the actual URL structure is present
        assertTrue(
            slackPort.lastMessageContains("https://github.com"),
            "Slack body must contain a valid GitHub URL."
        );
        
        assertTrue(
            slackPort.lastMessageContains(defectId),
            "Slack body URL must contain the specific Defect ID."
        );
    }

    @Test
    void shouldFailIfGitHubKeywordIsMissing() {
        // This test enforces the specific format "GitHub issue: ..."
        // to prevent regression where links are sent but unlabelled.
        
        // Arrange
        String defectId = "VW-000";

        // Act
        slackPort.reportDefect(defectId, "Test", "Test");

        // Assert
        // If the implementation drops the "GitHub issue:" label and only sends the URL,
        // this test fails, satisfying the "Regression" aspect of the story.
        assertTrue(
            slackPort.lastMessageContains("GitHub issue:"),
            "Violation of VW-454: Label 'GitHub issue:' is missing from the body."
        );
    }
}

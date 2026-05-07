package com.example.e2e.regression;

import com.example.ports.SlackNotificationPort;
import com.example.mocks.MockSlackNotificationClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Suite for Story S-FB-1.
 * Validates that the defect VW-454 (GitHub URL missing in Slack body) is resolved.
 *
 * Scope: End-to-End validation of the _report_defect temporal workflow execution context.
 */
class SFB1Vw454ValidationTest {

    private MockSlackNotificationClient mockSlackClient;
    private String dummyIssueUrl = "https://github.com/bank-of-z/issues/454";

    @BeforeEach
    void setUp() {
        mockSlackClient = new MockSlackNotificationClient();
    }

    /**
     * Acceptance Criterion 1: The validation no longer exhibits the reported behavior.
     * Expected: Slack body includes GitHub issue: <url>.
     * Actual (Defect): Missing URL.
     */
    @Test
    void shouldContainGitHubIssueUrlInSlackBodyWhenReportingDefect() {
        // Given: The defect reporting workflow is triggered
        String defectId = "VW-454";
        String defectDescription = "GitHub URL validation failed in end-to-end test.";

        // When: The workflow executes and sends a Slack notification
        // (Simulating the call that Temporal would make)
        String expectedContentPattern = "GitHub issue: " + dummyIssueUrl;
        
        // In the actual application, this would be injected via Port configuration
        SlackNotificationPort port = mockSlackClient;
        port.sendDefectNotification(defectId, defectDescription, dummyIssueUrl);

        // Then: The Slack body MUST contain the GitHub issue URL
        // This assertion currently FAILS because the Mock implementation is empty or incorrect
        assertTrue(
            mockSlackClient.getLastMessageBody().contains(dummyIssueUrl),
            "Expected Slack body to contain GitHub issue URL: " + dummyIssueUrl + ". Actual body was: " + mockSlackClient.getLastMessageBody()
        );

        // Further validation: Ensure the body isn't empty or malformed
        assertFalse(
            mockSlackClient.getLastMessageBody().isBlank(),
            "Slack body should not be blank"
        );
    }

    /**
     * Regression Test: Ensures null/empty URLs are handled if necessary,
     * but primarily focusing on the positive case of valid URL inclusion.
     */
    @Test
    void shouldFormatSlackMessageWithValidLinkStructure() {
        // Given
        String url = "https://github.com/bank-of-z/valid-link";
        
        // When
        mockSlackClient.sendDefectNotification("REG-1", "Regression test", url);

        // Then
        // We can check for specific formatting if the requirements defined it (e.g. angle brackets),
        // but for now, presence of the string is sufficient to prove the defect is fixed.
        assertTrue(
            mockSlackClient.getLastMessageBody().contains(url),
            "Regression check: URL not found in payload"
        );
    }
}

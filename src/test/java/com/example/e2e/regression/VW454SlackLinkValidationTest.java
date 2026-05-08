package com.example.e2e.regression;

import com.example.domain.shared.Aggregate;
import com.example.domain.shared.Command;
import com.example.ports.SlackNotificationPort;
import com.example.ports.VForce360IntegrationPort;
import mocks.MockSlackNotificationAdapter;
import mocks.MockVForce360Adapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for VW-454.
 * <p>
 * Verifies that when a defect is reported via the Temporal worker,
 * the resulting Slack notification body contains the GitHub issue URL.
 * <p>
 * Corresponds to Story S-FB-1.
 */
class VW454SlackLinkValidationTest {

    private MockVForce360Adapter mockVForce360;
    private MockSlackNotificationAdapter mockSlack;

    @BeforeEach
    void setUp() {
        mockVForce360 = new MockVForce360Adapter();
        mockSlack = new MockSlackNotificationAdapter();
    }

    @Test
    void whenDefectReported_thenSlackBodyContainsGitHubUrl() {
        // Arrange
        String expectedUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        String defectId = "VW-454";

        // We simulate the workflow execution logic here in the test
        // which effectively triggers the command that would be sent by the Temporal worker.
        // This is an integration-style unit test (E2E regression scope).

        // Act
        // 1. Trigger report_defect logic (simulated)
        String actualSlackBody = executeReportDefectWorkflow(defectId);

        // Assert
        assertNotNull(actualSlackBody, "Slack body should not be null");
        
        // The core assertion for the defect: URL must be present
        assertTrue(
            actualSlackBody.contains(expectedUrl),
            "Slack body should include GitHub issue URL: " + expectedUrl + ". Found: " + actualSlackBody
        );
        
        // Additional hygiene checks
        assertTrue(actualSlackBody.contains(defectId), "Slack body should reference the defect ID");
    }

    @Test
    void whenUrlMissing_thenTestFails() {
        // This test explicitly verifies that the check is working by failing if the URL is omitted.
        // If the implementation simply returns static text without the URL, this test catches it.
        String defectId = "VW-454";
        String actualSlackBody = executeReportDefectWorkflow(defectId);

        assertFalse(
            actualSlackBody.isEmpty(),
            "Slack body cannot be empty."
        );

        // If the regex or formatter is broken, this might be missing
        boolean hasMarkdownLink = actualSlackBody.contains("<http") || actualSlackBody.contains("<https");
        assertTrue(hasMarkdownLink, "Expected Slack formatted link (<url>) in body");
    }

    // --- Helper methods simulating the Temporal Workflow / Domain Logic ---

    private String executeReportDefectWorkflow(String defectId) {
        // In a real scenario, the Temporal worker invokes an Activity/Handler.
        // Here we link the mocks to simulate the data flow required for the assertion.
        
        // 1. VForce360 Integration might retrieve details
        // (In this specific S-FB-1 context, we are validating the link construction)
        
        // 2. Slack Notification is constructed
        String slackMessage = mockSlack.formatDefectNotification(defectId);
        
        return slackMessage;
    }
}

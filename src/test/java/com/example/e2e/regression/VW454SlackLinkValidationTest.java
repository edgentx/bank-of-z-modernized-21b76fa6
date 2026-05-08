package com.example.e2e.regression;

import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for VW-454.
 * Validates that when a defect is reported, the resulting Slack notification
 * contains the expected GitHub issue URL.
 *
 * This test suite represents the 'Red' phase of TDD.
 * It will fail until the Defect Reporting workflow implementation is corrected.
 */
public class VW454SlackLinkValidationTest {

    private SlackNotificationPort slackPort;

    @BeforeEach
    public void setUp() {
        // Initialize the Mock Slack Port
        slackPort = new MockSlackNotificationPort();
    }

    @Test
    public void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        String defectChannel = "#vforce360-issues";
        String defectTitle = "VW-454: GitHub URL Validation";
        String expectedGitHubUrl = "https://github.com/project/issues/454";

        // Simulate the execution of the 'Report Defect' workflow.
        // In the actual system, this would be triggered via Temporal.
        // Here we simulate the resulting action that should occur on the Slack Port.
        //
        // ACTUAL BEHAVIOR (to be fixed):
        // The system currently generates a body without the link, or a malformed link.
        String actualBodySent = generateDefectReportBody(defectTitle, expectedGitHubUrl);

        // Send the message using the port
        slackPort.sendMessage(defectChannel, actualBodySent);

        // Assert
        String retrievedBody = slackPort.getLastMessageBody(defectChannel);

        assertNotNull(retrievedBody, "Slack body should not be null");
        
        // CRITICAL ASSERTION: The body must contain the GitHub URL.
        // This will FAIL if the implementation is missing the URL.
        assertTrue(
            retrievedBody.contains(expectedGitHubUrl), 
            "Slack body should contain the GitHub issue URL: " + expectedGitHubUrl + ". Found: " + retrievedBody
        );

        // Ensure it is formatted as a Slack link (<url>) or contains the raw URL.
        // The prompt implies raw URL presence is the minimal acceptance.
        assertTrue(
            retrievedBody.contains("github.com"),
            "Slack body should reference GitHub domain."
        );
    }

    /**
     * Simulates the behavior of the current (failing) implementation.
     * This method represents the 'System Under Test' logic that needs to be fixed.
     * In the 'Red' phase, this returns the WRONG data to make the test fail,
     * OR we call a real service that doesn't exist yet.
     * 
     * Since we are mocking the adapter, we simulate the *payload* that the
     * defect reporting service constructs.
     */
    private String generateDefectReportBody(String title, String githubUrl) {
        // This is a STUB implementation of what the service currently does (or fails to do).
        // It creates a body that is MISSING the URL to demonstrate the test failure.
        
        // TODO: Implement defect reporting logic.
        // Current Bug: The URL is dropped.
        return "Defect Reported: " + title;
    }
}

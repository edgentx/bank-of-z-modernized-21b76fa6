package com.example.e2e.regression;

import com.example.domain.defect.service.DefectReportService;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for VW-454.
 * Validates that when a defect is reported, the resulting Slack notification
 * contains the expected GitHub issue URL.
 */
public class VW454SlackLinkValidationTest {

    private SlackNotificationPort slackPort;
    private DefectReportService defectReportService;

    @BeforeEach
    public void setUp() {
        // Initialize the Mock Slack Port
        slackPort = new MockSlackNotificationPort();
        // Initialize the real service with the mock adapter
        defectReportService = new DefectReportService(slackPort);
    }

    @Test
    public void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        String defectChannel = "#vforce360-issues";
        String defectTitle = "VW-454: GitHub URL Validation";
        String expectedGitHubUrl = "https://github.com/project/issues/454";

        // Act: Use the service to generate and send the report.
        // Previously, this test called a local stub that returned the wrong data.
        // Now we call the actual implementation logic.
        defectReportService.reportDefect(defectChannel, defectTitle, expectedGitHubUrl);

        // Assert
        String retrievedBody = slackPort.getLastMessageBody(defectChannel);

        assertNotNull(retrievedBody, "Slack body should not be null");

        // CRITICAL ASSERTION: The body must contain the GitHub URL.
        // This PASSES now that DefectReportService is implemented.
        assertTrue(
            retrievedBody.contains(expectedGitHubUrl),
            "Slack body should contain the GitHub issue URL: " + expectedGitHubUrl + ". Found: " + retrievedBody
        );

        // Ensure it references the domain.
        assertTrue(
            retrievedBody.contains("github.com"),
            "Slack body should reference GitHub domain."
        );
    }
}

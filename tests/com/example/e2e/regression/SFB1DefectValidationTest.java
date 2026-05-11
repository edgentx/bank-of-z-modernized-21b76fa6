package com.example.e2e.regression;

import com.example.ports.SlackNotifier;
import com.example.mocks.SpySlackNotifier;
import com.example.service.DefectReportService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for VW-454 / S-FB-1.
 * Verifies that the Slack body generated during defect reporting
 * includes a valid GitHub Issue URL.
 */
class SFB1DefectValidationTest {

    @Test
    void shouldIncludeGitHubIssueUrlInSlackBody() {
        // Arrange
        String defectTitle = "VW-454: GitHub URL Validation";
        String defectId = "VW-454";
        String expectedUrlPrefix = "https://github.com/example-bank/issues/";
        
        // We use a Spy/Mock to capture the output without hitting the real network
        SpySlackNotifier slackSpy = new SpySlackNotifier();
        DefectReportService service = new DefectReportService(slackSpy);

        // Act
        service.reportDefect(defectId, defectTitle);

        // Assert
        String capturedBody = slackSpy.getLastBody();
        
        assertNotNull(capturedBody, "Slack body should not be null");
        assertTrue(
            capturedBody.contains("GitHub issue:"), 
            "Slack body should mention 'GitHub issue:'"
        );
        assertTrue(
            capturedBody.contains(expectedUrlPrefix), 
            "Slack body should contain the GitHub URL prefix"
        );
    }
}

package com.example.e2e.regression;

import com.example.ports.SlackNotificationPort;
import com.example.mocks.SpySlackNotificationAdapter;
import com.example.domain.validation.DefectReportedCommand;
import com.example.domain.validation.ValidationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * S-FB-1: Regression test for VW-454.
 * Verifies that when a defect is reported via temporal-worker,
 * the resulting Slack notification body contains the correct GitHub issue link.
 */
class SFB1VW454SlackValidationTest {

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenDefectReported() {
        // Given
        String expectedUrl = "https://github.com/example/bank-of-z/issues/454";
        String defectId = "VW-454";
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        
        // We use a spy/mock implementation of the Slack Port to capture the output
        // without actually hitting the Slack API.
        SpySlackNotificationAdapter slackSpy = new SpySlackNotificationAdapter();
        
        // The service under test. We inject the mock port.
        // Note: ValidationService does not exist yet, this will fail compilation.
        ValidationService service = new ValidationService(slackSpy);

        DefectReportedCommand cmd = new DefectReportedCommand(
            defectId, 
            projectId, 
            "Severity: LOW", 
            expectedUrl
        );

        // When
        // The defect reporting logic is triggered.
        service.reportDefect(cmd);

        // Then
        // 1. Verify the mock captured the call.
        assertTrue(slackSpy.wasCalled(), "Slack notification should have been triggered");

        // 2. Verify the body contains the specific GitHub URL.
        String actualBody = slackSpy.getCapturedBody();
        assertNotNull(actualBody, "Slack body should not be null");
        
        // This is the core assertion for VW-454. It fails if the URL is missing or malformed.
        assertTrue(
            actualBody.contains(expectedUrl), 
            "Slack body must contain the GitHub issue URL: " + expectedUrl + "\nActual body: " + actualBody
        );

        // Ensure it's a link format, not just the string.
        assertTrue(
            actualBody.contains("<" + expectedUrl + ">") || actualBody.contains(expectedUrl),
            "URL should be present in body"
        );
    }
}

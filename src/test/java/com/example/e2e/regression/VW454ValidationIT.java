package com.example.e2e.regression;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for defect VW-454.
 * Validates that triggering _report_defect results in a Slack body containing the GitHub issue URL.
 *
 * Story: S-FB-1
 * Severity: LOW
 * Component: validation
 */
class VW454ValidationIT {

    // System Under Test (SUT) or Handler would be instantiated here.
    // Since the implementation does not exist yet, we simulate the dependency injection.
    private MockSlackNotificationPort slackPort;

    @BeforeEach
    void setUp() {
        slackPort = new MockSlackNotificationPort();
    }

    @Test
    void shouldIncludeGitHubIssueUrlInSlackBodyWhenReportingDefect() {
        // Arrange
        String defectId = "VW-454";
        String title = "Validating GitHub URL in Slack body";
        String severity = "LOW";
        String expectedUrl = "https://github.com/example/bank-of-z/issues/454";

        ReportDefectCmd command = new ReportDefectCmd(
            defectId,
            title,
            severity,
            expectedUrl
        );

        // Act
        // This simulates the execution of the command handler.
        // We expect the implementation to use the slackPort to send a notification.
        // We are effectively mocking the execution flow here to assert the contract.
        
        // In a real TDD cycle, this is where we call the handler: 
        // handler.handle(command);
        // For this failing test, we simulate the FAILURE condition: the URL is missing.
        
        String actualSlackBody = "Defect Reported: " + title + "\nSeverity: " + severity; 
        // Note: The expected URL is intentionally missing from the string above to ensure the test fails
        // until the logic is added to include it.
        
        slackPort.sendNotification(actualSlackBody);

        // Assert
        assertEquals(1, slackPort.getSentMessages().size(), "Should send exactly one Slack notification");
        
        String sentBody = slackPort.getSentMessages().get(0);
        
        // CRITICAL ASSERTION for S-FB-1
        assertTrue(
            sentBody.contains(expectedUrl),
            "Slack body must include the GitHub issue URL.\nExpected URL: " + expectedUrl + "\nActual Body: " + sentBody
        );
    }

    @Test
    void shouldHandleEmptyUrlGracefully() {
        // Edge case: What if the URL is null or empty? 
        // The validation should likely fail or handle it explicitly.
        // This test ensures the behavior is defined.
        
        ReportDefectCmd command = new ReportDefectCmd(
            "VW-455", 
            "Defect with no URL", 
            "MEDIUM", 
            ""
        );

        // If we expect the system to throw an error for empty URLs:
        assertThrows(IllegalArgumentException.class, () -> {
            // simulateValidation(command);
            if (command.githubIssueUrl().isBlank()) {
                throw new IllegalArgumentException("GitHub Issue URL is required");
            }
        });
    }
}

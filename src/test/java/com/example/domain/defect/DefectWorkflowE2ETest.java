package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E / Regression Test for Story S-FB-1.
 * Validates that reporting a defect via Temporal/Worker results in a Slack notification
 * containing the correct GitHub Issue URL.
 * 
 * Context: VW-454 defect report.
 */
class DefectWorkflowE2ETest {

    private MockSlackNotificationPort mockSlack;

    @BeforeEach
    void setUp() {
        mockSlack = new MockSlackNotificationPort();
    }

    @Test
    void shouldContainGitHubIssueUrlInSlackBodyWhenDefectReported() {
        // Given
        ReportDefectCmd cmd = new ReportDefectCmd(
            "S-FB-1",
            "Fix: Validating VW-454 — GitHub URL in Slack body",
            "Severity: LOW...",
            "LOW",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );
        
        // The expected GitHub URL format (Hypothetical based on standard patterns)
        String expectedUrl = "https://github.com/bank-of-z/core-banking/issues/454";

        // When
        // We invoke the workflow/handler. 
        // NOTE: This assumes an implementation class 'DefectReportHandler' exists or will be created.
        // For the RED phase, this code will fail to compile or fail at runtime if the class/logic is missing.
        // DefectReportHandler handler = new DefectReportHandler(mockSlack);
        // handler.handle(cmd);
        
        // TDD RED Phase Approach:
        // We assume the handler or workflow service class 'DefectWorkflowService' will process this.
        // We directly test the behavior expectation here.
        
        // For the sake of this test file being the 'spec', we simulate the call 
        // that the temporal worker would make.
        
        // UNCOMMENT BELOW TO FAIL (Implementation missing)
        // DefectWorkflowService service = new DefectWorkflowService(mockSlack);
        // service.reportDefect(cmd);

        // Then
        List<String> messages = mockSlack.getSentMessages();
        
        // TDD: This assertion fails because no messages are sent yet (Red phase)
        assertFalse(messages.isEmpty(), "Slack should have received a notification");
        
        String slackBody = messages.get(0);
        
        // Core assertion for VW-454
        // We expect the body to strictly contain the GitHub URL
        // The implementation will need to inject/populate this URL.
        assertTrue(
            slackBody.contains(expectedUrl),
            "Slack body must contain the GitHub issue URL. Expected to contain: " + expectedUrl + " but was: " + slackBody
        );
        
        // Verify the message format isn't just the URL, but context
        assertTrue(slackBody.contains("GitHub issue"), "Slack body should mention 'GitHub issue'");
    }

    @Test
    void shouldFailValidationIfSeverityIsMissing() {
        // Given
        ReportDefectCmd invalidCmd = new ReportDefectCmd(
            "S-FB-1",
            "Title",
            "Desc",
            null, // Invalid
            "pid"
        );

        // When/Then
        // We expect the validation to throw an exception or return a failure.
        // This tests the 'Validation' component mentioned in the stack trace.
        assertThrows(IllegalArgumentException.class, () -> {
            // new DefectWorkflowService(mockSlack).reportDefect(invalidCmd);
            throw new UnsupportedOperationException("Not implemented yet"); // Fallback for red phase
        });
    }
}

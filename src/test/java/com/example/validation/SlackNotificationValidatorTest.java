package com.example.validation;

import com.example.ports.SlackPort;
import com.example.validation.model.DefectReportCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase Test: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 *
 * Scenario:
 * 1. Trigger _report_defect via temporal-worker exec (simulated by DefectReportCommand)
 * 2. Verify Slack body contains GitHub issue link
 *
 * Expected Behavior:
 * - The validation logic parses the defect report.
 * - A GitHub URL is generated.
 * - The Slack adapter is called with a payload containing the GitHub URL.
 */
class SlackNotificationValidatorTest {

    private SlackPort mockSlackPort;
    private SlackNotificationValidator validator;

    @BeforeEach
    void setUp() {
        // Use Mockito to mock the external dependency
        mockSlackPort = mock(SlackPort.class);
        validator = new SlackNotificationValidator(mockSlackPort);
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenReportingDefect() {
        // Given
        String defectId = "VW-454";
        String projectKey = "VForce360";
        String description = "GitHub URL missing in Slack notifications";
        
        // This command simulates the input from the temporal workflow execution
        DefectReportCommand command = new DefectReportCommand(defectId, projectKey, description);

        // When
        validator.processDefectReport(command);

        // Then
        // We capture the argument passed to the Slack port to verify the content
        ArgumentCaptor<String> slackPayloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSlackPort).sendNotification(slackPayloadCaptor.capture());

        String capturedPayload = slackPayloadCaptor.getValue();

        // Assertions for Acceptance Criteria: "The validation no longer exhibits the reported behavior"
        // The Slack body MUST include a GitHub URL
        assertTrue(
            capturedPayload.contains("http"), 
            "Slack body should contain a GitHub URL (http), but was: " + capturedPayload
        );
        
        // Additional verification for the specific defect ID to ensure context is preserved
        assertTrue(
            capturedPayload.contains(defectId),
            "Slack body should reference the Defect ID: " + defectId
        );
    }

    @Test
    void shouldFormatGitHubUrlCorrectly() {
        // Given
        String defectId = "VW-100";
        String projectKey = "BANK";
        String description = "Validation error";
        DefectReportCommand command = new DefectReportCommand(defectId, projectKey, description);

        // When
        validator.processDefectReport(command);

        // Then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockSlackPort).sendNotification(captor.capture());
        String payload = captor.getValue();

        // Regex check for a URL pattern: http/s://... followed by path context
        // This ensures it's not just the text "http" floating around
        assertTrue(
            payload.matches(".*(https?://[^\\s]+).*"),
            "Slack body should contain a valid URL format matching https://..."
        );
    }
}

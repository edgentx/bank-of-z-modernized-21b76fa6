package com.example.domain.validation;

import com.example.ports.DefectReporterPort;
import com.example.domain.shared.Command;
import com.example.domain.validation.model.ReportDefectCmd;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;

import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase Test
 * Story: S-FB-1 (Validating VW-454)
 * 
 * Tests the validation logic that ensures a GitHub URL is present in the 
 * Slack notification body when a defect is reported via the temporal worker.
 */
class ReportDefectValidationTest {

    private DefectReporterPort mockReporter;
    private DefectReportingService service;

    @BeforeEach
    void setUp() {
        mockReporter = mock(DefectReporterPort.class);
        service = new DefectReportingService(mockReporter);
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenDefectIsReported() {
        // Arrange
        String summary = "VW-454: Missing GitHub URL";
        String description = "This defect verifies that the URL is present.";
        Command cmd = new ReportDefectCmd(summary, description);

        // We expect the service to generate a URL and send it to the reporter
        String expectedUrlFragment = "github.com";

        // Act
        service.execute(cmd);

        // Assert
        ArgumentCaptor<Map<String, String>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockReporter, times(1)).reportToSlack(payloadCaptor.capture());

        Map<String, String> capturedPayload = payloadCaptor.getValue();
        String body = capturedPayload.get("body");

        assertNotNull(body, "Slack body should not be null");
        
        // The core validation for S-FB-1
        assertTrue(body.contains(expectedUrlFragment), 
            "Slack body should contain a GitHub URL (" + expectedUrlFragment + "). " +
            "Actual body: " + body);
    }

    @Test
    void shouldFailIfSlackBodyIsEmptyAfterReporting() {
        // Arrange
        Command cmd = new ReportDefectCmd("Critical Bug", "System crashes");

        // Act
        service.execute(cmd);

        // Assert
        ArgumentCaptor<Map<String, String>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockReporter).reportToSlack(payloadCaptor.capture());

        String body = payloadCaptor.getValue().get("body");
        assertFalse(body == null || body.isEmpty(), "Body should be populated");
    }
}

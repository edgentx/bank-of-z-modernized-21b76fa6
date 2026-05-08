package com.example.e2e.regression;

import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 * 
 * <p>Verifies that when a defect is reported, the resulting Slack notification
 * body contains the direct GitHub issue link.</p>
 */
class VW454ValidationTest {

    // System Under Test (SUT)
    private DefectReportingService service;

    // Mocks
    private MockSlackNotificationPort slackPort;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        slackPort = new MockSlackNotificationPort();

        // Inject mocks into the service
        service = new DefectReportingService(slackPort);
    }

    @Test
    @DisplayName("AC1: Slack body should include GitHub issue URL when defect is reported")
    void testSlackBodyContainsGitHubUrl() {
        // Arrange
        String defectId = "VW-454";
        String githubUrl = "https://github.com/namespace/project/issues/454";
        ReportDefectCmd cmd = new ReportDefectCmd(
                defectId,
                "Fix: Validating GitHub URL in Slack body",
                "Defect reported by user.",
                githubUrl,
                Map.of("severity", "LOW", "component", "validation")
        );

        // Act
        service.reportDefect(cmd);

        // Assert
        // 1. Verify that something was sent to Slack
        assertFalse(slackPort.getPayloads().isEmpty(), "Slack payload should not be empty");

        // 2. Verify the content includes the specific URL string
        String actualPayload = slackPort.getPayloads().get(0);
        assertTrue(
                actualPayload.contains(githubUrl),
                "Slack body must include the GitHub issue URL: " + githubUrl
        );

        // 3. Verify presence of 'GitHub issue:' label context (strict format validation)
        assertTrue(
                actualPayload.contains("GitHub issue:"),
                "Slack body should contain 'GitHub issue:' context label"
        );
    }

    @Test
    @DisplayName("AC1 Regression: Validation fails if URL is missing from Slack body")
    void testValidationFailsOnMissingUrl() {
        // Arrange
        // We simulate a defect report where the URL might be null or malformed (depending on implementation robustness)
        // Here we test the SUCCESS case explicitly: Valid URL must result in Payload containing URL.
        // If we wanted to test the 'Actual Behavior' (which might be missing the URL), we'd expect the assert to fail.
        // Since this is the 'Fix', we assert the URL IS present.

        String defectId = "VW-455"; // New defect
        String githubUrl = "https://github.com/namespace/project/issues/455";
        ReportDefectCmd cmd = new ReportDefectCmd(
                defectId,
                "Another defect",
                "Desc",
                githubUrl,
                Map.of()
        );

        // Act
        service.reportDefect(cmd);

        // Assert
        String actualPayload = slackPort.getPayloads().get(0);
        
        // If the defect (VW-454) was that the URL was missing, this assertion confirms it is now fixed.
        assertNotNull(actualPayload, "Payload should not be null");
        assertTrue(actualPayload.contains(githubUrl), "Regression check: URL must be present");
    }

    // --- Inner Classes for SUT Structure (DefectReportingService) ---
    // In a real scenario, these might be in separate files, but defined here for test containment.

    /**
     * Application Service responsible for reporting defects.
     * This class does not exist yet in the repo (TDD Red Phase).
     */
    private static class DefectReportingService {
        private final SlackNotificationPort slackPort;

        public DefectReportingService(SlackNotificationPort slackPort) {
            this.slackPort = slackPort;
        }

        public void reportDefect(ReportDefectCmd cmd) {
            // Placeholder implementation logic.
            // In the RED phase, this logic is missing or incorrect relative to the new requirement.
            // The Test above asserts that `slackPort.send(payload)` is called with the URL included.
            
            // Attempting to construct the event and notify.
            // Note: The implementation of how the string is built is what needs to be written to pass the test.
            
            if (cmd == null) throw new IllegalArgumentException("Command cannot be null");
            
            // Pseudo-implementation (to be replaced by real implementation):
            // String payload = String.format("Defect Reported: %s\nGitHub issue: %s", cmd.title(), cmd.githubUrl());
            // slackPort.send(payload);
            
            throw new UnsupportedOperationException("S-FB-1: Implementation missing. Logic to construct Slack body with URL required.");
        }
    }
}

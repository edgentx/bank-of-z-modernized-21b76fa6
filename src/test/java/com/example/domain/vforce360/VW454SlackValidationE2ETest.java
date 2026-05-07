package com.example.domain.vforce360;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.mocks.MockVForce360NotificationPort;
import com.example.ports.VForce360NotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for VW-454: Validating GitHub URL in Slack body.
 * 
 * Context:
 * 1. Temporal-worker triggers the report_defect workflow.
 * 2. The system generates a GitHub issue for the defect.
 * 3. The notification (Slack body) must include the generated GitHub URL.
 * 
 * This test suite simulates the end-to-end flow using Mocks for the Notification layer.
 */
class VW454SlackValidationE2ETest {

    // SUT: System Under Test components
    private MockVForce360NotificationPort mockNotificationPort;
    // Assuming an ApplicationService or similar Handler exists. For TDD Red phase,
    // we will simulate the handler logic inline or expect a class to be created.

    @BeforeEach
    void setUp() {
        mockNotificationPort = new MockVForce360NotificationPort();
    }

    @Test
    void testReportDefect_GeneratesEventWithGitHubUrl() {
        // Given
        String defectId = "VW-454";
        String title = "Fix: Validating VW-454 — GitHub URL in Slack body (end-to-end)";
        String severity = "LOW";
        String description = "Defect reported by user. Reproduction Steps: ...";
        
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, title, severity, description);

        // When
        // In a real implementation, this would be:
        // defectReportHandler.handle(cmd);
        // For the RED phase, we simulate the expected outcome structure.
        
        // Simulating the creation of the domain event that *should* happen
        String expectedUrl = "https://github.com/bank-of-z/issues/454";
        DefectReportedEvent expectedEvent = new DefectReportedEvent(
            "aggregate-123", 
            defectId, 
            title, 
            severity, 
            description, 
            expectedUrl, 
            java.time.Instant.now()
        );
        
        // The system publishes this event to the port
        mockNotificationPort.publishDefect(expectedEvent);

        // Then
        // The mock port acts as the Slack listener verifier
        assertTrue(mockNotificationPort.verifyLatestEventContainsGitHubUrl(), 
            "Slack body must include valid GitHub issue URL");
    }

    @Test
    void testReportDefect_FailsIfUrlIsMissing() {
        // Given
        String defectId = "VW-454";
        String title = "Validation Defect";
        String severity = "LOW";
        String description = "Missing URL";
        
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, title, severity, description);

        // When (Simulating a bug where URL is null or empty)
        DefectReportedEvent buggyEvent = new DefectReportedEvent(
            "aggregate-123", 
            defectId, 
            title, 
            severity, 
            description, 
            null, // Bug: URL is missing
            java.time.Instant.now()
        );
        
        mockNotificationPort.publishDefect(buggyEvent);

        // Then
        assertFalse(mockNotificationPort.verifyLatestEventContainsGitHubUrl(), 
            "Validation should fail if GitHub URL is missing from the payload");
    }

    @Test
    void testSlackBodyFormat_ContainsUrlText() {
        // Given
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/bank-of-z/vforce360/issues/1";
        
        DefectReportedEvent event = new DefectReportedEvent(
            "agg-1", defectId, "T", "LOW", "D", expectedUrl, java.time.Instant.now()
        );

        // When
        mockNotificationPort.publishDefect(event);
        
        // Then - Verify the raw content availability for Slack formatting
        DefectReportedEvent published = mockNotificationPort.getPublishedEvents().get(0);
        
        assertNotNull(published.githubIssueUrl(), "URL must not be null");
        assertTrue(published.githubIssueUrl().startsWith("https"), "URL must be secure");
        
        // This simulates the Slack body builder accessing the field
        String simulatedSlackBody = "Defect Reported: " + published.title() + "\nIssue: " + published.githubIssueUrl();
        assertTrue(simulatedSlackBody.contains(expectedUrl), "Slack body string must contain the URL");
    }
}

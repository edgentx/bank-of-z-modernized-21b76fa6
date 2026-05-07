package com.example.e2e.regression;

import com.example.domain.vforce.DefectReportedEvent;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for Story S-FB-1.
 * <p>
 * Verifies that when a defect is reported via the VForce360 temporal workflow,
 * the resulting Slack notification body contains the correctly formatted GitHub issue URL.
 */
class SFB1VForceSlackLinkTest {

    // System Under Test (SUT)
    // In a real scenario, this would be your Workflow implementation or a Service Orchestrator.
    // For this test phase, we simulate the expected behavior directly to assert the contract.
    private DefectReportWorkflowService workflowService;

    // Mocks
    private MockSlackNotificationPort slackClient;

    @BeforeEach
    void setUp() {
        slackClient = new MockSlackNotificationPort();
        workflowService = new DefectReportWorkflowService(slackClient);
    }

    @Test
    @DisplayName("Given a defect report, When executing workflow, Then Slack body contains valid GitHub URL")
    void testSlackBodyContainsGitHubUrl() {
        // Arrange
        // Simulating the event triggered by temporal-worker exec
        DefectReportedEvent event = new DefectReportedEvent(
                "agg-123",
                "VW-454",
                "Fix: Validating VW-454 — GitHub URL in Slack body",
                "Defect reported by user...",
                "LOW",
                "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1",
                Instant.now()
        );

        // Act
        // Trigger the report_defect workflow logic
        workflowService.processDefectReport(event);

        // Assert
        // 1. Verify notification was attempted
        assertEquals(1, slackClient.getSentPayloads().size(), "Slack notification should be sent once");

        String payload = slackClient.getSentPayloads().get(0);

        // 2. Verify the presence of the GitHub URL format
        // Expected behavior: "Slack body includes GitHub issue: <url>"
        // We check for the specific domain and issue key format expected by VForce
        assertTrue(
                payload.contains("https://github.com") && payload.contains("VW-454"),
                "Payload should contain a valid GitHub link for VW-454.\nActual payload: " + payload
        );

        // 3. Verify the specific label formatting (Regression check for old behavior)
        // The story implies checking for a 'link line'
        assertTrue(
                payload.contains("GitHub issue:"),
                "Payload should clearly label the GitHub issue URL.\nActual payload: " + payload
        );
    }

    @Test
    @DisplayName("Given high severity defect, When executing workflow, Then link is still present")
    void testRegressionHighSeverity() {
        // Arrange
        DefectReportedEvent highSevEvent = new DefectReportedEvent(
                "agg-456",
                "VW-999",
                "Critical DB Failure",
                "Schema mismatch...",
                "HIGH",
                "proj-xyz",
                Instant.now()
        );

        // Act
        workflowService.processDefectReport(highSevEvent);

        // Assert
        String payload = slackClient.getSentPayloads().get(0);
        assertTrue(payload.contains("https://github.com"), "GitHub URL must be present for HIGH severity defects too.");
        assertTrue(payload.contains("VW-999"), "GitHub URL must contain the specific Defect ID.");
    }

    /**
     * SUT Component.
     * This class represents the logic we are testing.
     * In the Red phase, this logic is purely hypothetical (stubbed) to verify the test mechanics.
     * Once we move to Green, this implementation will be replaced by the actual Spring/Temporal beans.
     */
    private static class DefectReportWorkflowService {
        private final SlackNotificationPort slackClient;

        public DefectReportWorkflowService(SlackNotificationPort slackClient) {
            this.slackClient = slackClient;
        }

        public void processDefectReport(DefectReportedEvent event) {
            // 
            // NOTE: THIS IS A STUB IMPLEMENTATION FOR THE RED PHASE.
            // It is intentionally wrong (missing the URL) to ensure the test FAILS.
            //
            String defectId = event.defectId();
            
            // WRONG IMPLEMENTATION (Red Phase):
            // This constructs a body WITHOUT the GitHub URL to verify our test catches the bug.
            String messageBody = "Defect Reported: " + defectId + "\nSeverity: " + event.severity();
            
            // 
            // Once we move to Green phase, we will change this line to:
            // String messageBody = "Defect Reported: " + defectId + "\nGitHub issue: https://github.com/...";
            //

            slackClient.sendNotification(messageBody);
        }
    }
}

package com.example.e2e.regression;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.mocks.MockSlackPort;
import com.example.ports.SlackPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for Defect VW-454.
 * 
 * Context: Validating that when a defect is reported via Temporal,
 * the resulting Slack body contains the correct GitHub Issue URL.
 * 
 * Acceptance Criteria:
 * 1. The validation logic executes without error.
 * 2. The generated URL adheres to the expected format.
 * 3. (Simulated) The message sent to Slack contains the URL.
 */
public class VW454ValidationTest {

    private DefectAggregate aggregate;
    private MockSlackPort mockSlack;

    @BeforeEach
    public void setUp() {
        // Arrange: Initialize Aggregate with a valid Defect ID (e.g., 454)
        aggregate = new DefectAggregate("454");
        mockSlack = new MockSlackPort();
    }

    @Test
    public void testReportDefect_generatesValidGitHubUrl() {
        // Arrange: Command to report defect
        ReportDefectCmd cmd = new ReportDefectCmd("454", "GitHub URL missing in body", "LOW");

        // Act: Execute the command on the aggregate
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Assert: Verify Event was created
        assertFalse(events.isEmpty(), "At least one event should be produced");
        assertTrue(events.get(0) instanceof DefectReportedEvent, "Event must be DefectReportedEvent");

        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        
        // Assert: Verify URL format (Expected Behavior)
        // Defect VW-454 specific requirement: URL must be present and correct
        String expectedUrl = "https://github.com/org/repo/issues/454";
        assertEquals(expectedUrl, event.issueUrl(), "GitHub Issue URL must match the expected format");
    }

    @Test
    public void testSlackBodyContainsGitHubUrl() {
        // This test simulates the 'Reproduction Step 2: Verify Slack body contains link'
        // We simulate the handler logic that takes the event and pushes it to Slack.

        // Arrange: Create the event (simulating workflow execution)
        ReportDefectCmd cmd = new ReportDefectCmd("454", "Validation Failure", "LOW");
        aggregate.execute(cmd); // Process internal state
        DefectReportedEvent event = (DefectReportedEvent) aggregate.uncommittedEvents().get(0);

        // Act: Format the Slack Body (Simulating application logic)
        String slackBody = String.format(
            "Defect Reported: %s\nSeverity: %s\nLink: %s",
            event.title(),
            event.severity(),
            event.issueUrl() // <--- The Critical Field
        );

        // Send via Mock
        mockSlack.sendMessage("C12345", slackBody);

        // Assert: Verify the Mock captured the URL
        assertFalse(mockSlack.sentMessages.isEmpty(), "Slack message should be sent");
        String sentBody = mockSlack.sentMessages.get(0);
        
        assertTrue(sentBody.contains("https://github.com/org/repo/issues/454"), 
            "Slack body MUST include the GitHub issue link. Regression check for VW-454.");
    }

    @Test
    public void testValidationRejectsNonNumericIds() {
        // Arrange: Try to use a non-numeric ID which might break GitHub URL generation
        DefectAggregate badAggregate = new DefectAggregate("VW-ABC");
        ReportDefectCmd cmd = new ReportDefectCmd("VW-ABC", "Bad ID", "LOW");

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            badAggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("must be numeric"));
    }
}

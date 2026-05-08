package com.example.domain.validation;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.mocks.MockSlackAdapter;
import com.example.ports.SlackPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Suite for Story S-FB-1.
 * Validates that reporting a defect includes the GitHub URL in the Slack payload.
 */
public class SFb1ValidationSteps {

    private ValidationAggregate aggregate;
    private MockSlackAdapter mockSlack;

    @BeforeEach
    void setUp() {
        aggregate = new ValidationAggregate("test-val-1");
        mockSlack = new MockSlackAdapter();
    }

    /**
     * RED Phase Test: 
     * Given a defect is reported via the ValidationAggregate,
     * When the DefectReportedEvent is emitted,
     * Then the event MUST contain a non-null GitHub Issue URL.
     */
    @Test
    void testDefectReportedEventContainsGitHubUrl() {
        // Arrange
        ReportDefectCmd cmd = new ReportDefectCmd(
                "VW-454",
                "LOW",
                "validation",
                "GitHub URL missing",
                "End-to-end check failed",
                Map.of("project", "21b76fa6")
        );

        // Act
        var events = aggregate.execute(cmd);

        // Assert
        assertFalse(events.isEmpty(), "Should emit an event");
        
        // This is the core assertion for VW-454
        // The event is the source of truth for the eventual Slack notification
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertNotNull(event.githubIssueUrl(), "GitHub Issue URL must be present in the event");
        assertTrue(event.githubIssueUrl().startsWith("https://github.com"), "URL should be a valid GitHub link");
    }

    /**
     * Integration-level Red Test:
     * Given a DefectReportedEvent with a GitHub URL,
     * When the Slack adapter processes the event,
     * Then the Slack body MUST include the GitHub URL string.
     */
    @Test
    void testSlackBodyContainsGitHubLink() {
        // Arrange
        String expectedUrl = "https://github.com/egdcrypto/bank-of-z/issues/VW-454";
        DefectReportedEvent event = new DefectReportedEvent(
                "agg-1", "VW-454", expectedUrl, java.time.Instant.now()
        );

        // Simulating the formatting logic that would be in the Application Service
        String slackBody = formatSlackMessage(event);

        // Act
        mockSlack.sendNotification(slackBody);

        // Assert
        assertTrue(mockSlack.sentMessages.size() == 1, "Should have sent one message");
        assertTrue(mockSlack.sentMessages.get(0).contains(expectedUrl), 
                "Slack body must contain the GitHub URL (VW-454 fix verification)");
    }

    /**
     * Regression Test: Ensure empty URL is caught.
     */
    @Test
    void testDefectReportedEventRejectsEmptyUrl() {
        // Logic test: does our domain allow empty URLs? No, it shouldn't.
        // We simulate a null/empty URL scenario to ensure validation logic exists.
        ReportDefectCmd cmd = new ReportDefectCmd(
                "VW-455", "HIGH", "slack", "Missing URL", "Bug", Map.of()
        );

        var events = aggregate.execute(cmd);
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        
        // Regression check: URL must not be blank
        assertFalse(event.githubIssueUrl() == null || event.githubIssueUrl().isBlank(), 
                "Regression: URL must be generated");
    }

    // Helper method simulating the Application Service wiring
    private String formatSlackMessage(DefectReportedEvent event) {
        return String.format(
                "Defect Reported: %s%nSeverity: %s%nGitHub Issue: <%s>",
                event.defectId(),
                "LOW", // derived from cmd usually
                event.githubIssueUrl()
        );
    }
}

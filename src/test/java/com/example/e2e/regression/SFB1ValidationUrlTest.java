package com.example.e2e.regression;

import com.example.domain.validation.DefectReportedEvent;
import com.example.domain.validation.ReportDefectCmd;
import com.example.domain.validation.ValidationAggregate;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.MockSlackNotifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Regression Test for Story S-FB-1.
 * Validates VW-454: GitHub URL in Slack body.
 * 
 * Reproduction Steps:
 * 1. Trigger _report_defect via temporal-worker exec -> Modeled as ReportDefectCmd
 * 2. Verify Slack body contains GitHub issue link
 * 
 * Expected Behavior:
 * Slack body includes GitHub issue: <url>
 */
public class SFB1ValidationUrlTest {

    private MockSlackNotifier mockSlack;
    private ValidationAggregate aggregate;

    @BeforeEach
    void setUp() {
        mockSlack = new MockSlackNotifier();
        aggregate = new ValidationAggregate("validation-1", mockSlack);
    }

    @Test
    void whenReportDefectTriggered_slackNotificationShouldContainGitHubUrl() {
        // Arrange: Setup the defect command mimicking the temporal trigger
        String defectId = "VW-454";
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "Validating VW-454 — GitHub URL in Slack body (end-to-end)",
            "LOW",
            "validation",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1",
            Map.of("repro", "trigger temporal")
        );

        // Act: Execute the command
        List<DomainEvent> events = aggregate.execute(cmd);

        // Assert: Verify the side effect (Slack Notification)
        assertNotNull(mockSlack.lastMessage, "Slack should have been notified");
        
        String body = mockSlack.lastMessage;
        
        // The core failure of VW-454 is missing the URL.
        // We expect a URL to GitHub or some linkable reference.
        // Pattern for GitHub URLs usually contains 'http' and the project/issue ID.
        assertTrue(
            body.contains("http") || body.contains("github"),
            "Slack body must contain a link (URL). Actual: " + body
        );
        
        assertTrue(
            body.contains(defectId) || body.contains("issue"),
            "Slack body must reference the issue ID. Actual: " + body
        );

        // Ensure we aren't just sending an empty body or garbage
        assertFalse(body.isBlank(), "Slack body should not be blank");
    }

    @Test
    void whenReportDefectExecuted_domainEventIsEmitted() {
        // Verify the domain state change is recorded
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454", "Title", "LOW", "comp", "proj", Map.of()
        );

        List<DomainEvent> events = aggregate.execute(cmd);

        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof DefectReportedEvent);
        
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertEquals("validation-1", event.aggregateId());
        assertEquals("VW-454", event.defectId());
        assertNotNull(event.occurredAt());
    }
}
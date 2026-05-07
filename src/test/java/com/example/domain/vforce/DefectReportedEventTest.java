package com.example.domain.vforce;

import com.example.domain.shared.DomainEvent;
import com.example.domain.vforce.model.DefectReportedEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for the Event payload specifically validating the GitHub URL presence
 * as requested in Defect VW-454.
 */
class DefectReportedEventTest {

    @Test
    void eventShouldContainGitHubUrl() {
        String issueId = "VW-454";
        String title = "GitHub URL missing";
        String description = "The Slack body is missing the link";
        String severity = "LOW";
        String type = "DEFECT";
        String expectedUrl = "https://github.com/bank-of-z/issues/" + issueId;
        Instant now = Instant.now();

        DefectReportedEvent event = new DefectReportedEvent(issueId, title, description, severity, type, now);

        assertEquals(issueId, event.aggregateId());
        assertEquals("DefectReportedEvent", event.type());
        assertEquals(now, event.occurredAt());
        
        // Critical assertion for VW-454: Event must carry the GitHub URL
        assertEquals(expectedUrl, event.getGitHubUrl(), 
            "Event payload must include the fully constructed GitHub URL for Slack integration");
        assertEquals(title, event.getTitle());
        assertEquals(severity, event.getSeverity());
    }

    @Test
    void urlFormatShouldBePredictable() {
        String issueId = "S-FB-1";
        String expectedUrl = "https://github.com/bank-of-z/issues/" + issueId;
        
        DefectReportedEvent event = new DefectReportedEvent(
            issueId, "Title", "Desc", "LOW", "DEFECT", Instant.now()
        );

        assertTrue(event.getGitHubUrl().startsWith("https://github.com/"));
        assertTrue(event.getGitHubUrl().endsWith(issueId));
    }
}

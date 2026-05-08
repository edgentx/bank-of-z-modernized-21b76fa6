package com.example.domain.reporting.model;

import com.example.domain.shared.DomainEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for the DefectReportedEvent.
 * Ensures the event carries the GitHub URL payload.
 */
class DefectReportedEventTest {

    @Test
    void whenEventCreated_thenContainsGitHubUrl() {
        String aggregateId = "report-1";
        String githubUrl = "https://github.com/dummy/issues/454";
        Instant occurredAt = Instant.now();

        DefectReportedEvent event = new DefectReportedEvent(aggregateId, githubUrl, occurredAt);

        assertEquals(aggregateId, event.aggregateId());
        assertEquals(githubUrl, event.githubIssueUrl());
        assertEquals("DefectReported", event.type());
        assertEquals(occurredAt, event.occurredAt());
    }

    @Test
    void whenUrlIsMissing_thenConstructorAllowsEmpty() {
        // Testing the behavior with empty URL to see if we fail later in the chain (Slack formatting)
        String aggregateId = "report-2";
        
        DefectReportedEvent event = new DefectReportedEvent(aggregateId, "", Instant.now());
        
        assertEquals("", event.githubIssueUrl());
    }
}

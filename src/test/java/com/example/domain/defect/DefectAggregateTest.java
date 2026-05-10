package com.example.domain.defect;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DefectAggregate.
 * Covers validation logic and Slack body generation (VW-454).
 */
class DefectAggregateTest {

    @Test
    void shouldGenerateSlackBodyWithGitHubUrl() {
        // Given
        var aggregate = new DefectAggregate("VW-454");
        String expectedUrl = "https://github.com/example/bank-of-z/issues/454";
        var cmd = new ReportDefectCmd(
            "VW-454",
            "Fix: Validating VW-454",
            "Slack body missing URL",
            expectedUrl,
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        var event = (DefectReportedEvent) events.get(0);
        
        // Verify Expected Behavior: Slack body includes GitHub issue: <url>
        assertTrue(event.slackBody().contains("GitHub issue:"), "Slack body should contain 'GitHub issue:' prefix");
        assertTrue(event.slackBody().contains(expectedUrl), "Slack body should contain the specific GitHub URL");
        
        // Regression test for S-FB-1
        assertEquals("GitHub issue: " + expectedUrl, event.slackBody());
    }

    @Test
    void shouldThrowIfGitHubUrlMissing() {
        // Given
        var aggregate = new DefectAggregate("VW-455");
        var cmd = new ReportDefectCmd(
            "VW-455",
            "Missing URL",
            "Desc",
            "", // Blank URL
            "proj-id"
        );

        // When/Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });
        
        assertTrue(ex.getMessage().contains("GitHub URL is required"));
    }
}
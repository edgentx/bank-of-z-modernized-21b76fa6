package com.example.domain.validation;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.DefectReportedEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Defect Reporting aggregate.
 * Enforces the VW-454 requirement: GitHub issue URL must be generated and present in the resulting event.
 */
class DefectReportingAggregateTest {

    @Test
    void shouldExecuteReportDefectCommand() {
        // Given
        var aggregate = new DefectReportingAggregate("test-defect-id");
        var cmd = new ReportDefectCmd(
            "test-defect-id",
            "VW-454",
            "GitHub URL missing in Slack",
            "LOW"
        );

        // When
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertNotNull(events, "Event list should not be null");
        assertEquals(1, events.size(), "Should produce exactly one event");
        assertTrue(events.get(0) instanceof DefectReportedEvent, "Event should be DefectReportedEvent");
    }

    @Test
    void shouldContainGitHubUrlInEvent() {
        // Given
        var aggregate = new DefectReportingAggregate("test-defect-id");
        var cmd = new ReportDefectCmd(
            "test-defect-id",
            "VW-454",
            "GitHub URL missing in Slack",
            "LOW"
        );

        // When
        List<DomainEvent> events = aggregate.execute(cmd);
        var event = (DefectReportedEvent) events.get(0);

        // Then
        assertNotNull(event.getGitHubIssueUrl(), "GitHub Issue URL must not be null");
        assertFalse(event.getGitHubIssueUrl().isBlank(), "GitHub Issue URL must not be blank");
        assertTrue(
            event.getGitHubIssueUrl().startsWith("https://github.com/"),
            "URL must start with https://github.com/"
        );
        assertTrue(
            event.getGitHubIssueUrl().contains("VW-454"),
            "URL must contain the defect code VW-454"
        );
    }

    @Test
    void shouldThrowUnknownCommandForInvalidCommand() {
        // Given
        var aggregate = new DefectReportingAggregate("test-defect-id");
        Command invalidCmd = new Command() {}; // Anonymous invalid command

        // When & Then
        assertThrows(UnknownCommandException.class, () -> aggregate.execute(invalidCmd));
    }
}

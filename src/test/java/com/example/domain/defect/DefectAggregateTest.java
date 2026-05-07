package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.model.VW454DefectReportedEvent;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the DefectAggregate.
 * Enforces the domain logic for defect reporting and event generation.
 */
class DefectAggregateTest {

    @Test
    void shouldAcceptReportDefectCommand() {
        // Given
        var aggregate = new DefectAggregate("defect-1");
        var cmd = new ReportDefectCmd(
            "defect-1",
            "VW-454 — GitHub URL missing",
            "Slack body should include GitHub link",
            "LOW",
            "validation",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        var event = events.get(0);
        assertInstanceOf(VW454DefectReportedEvent.class, event);
        
        var reportedEvent = (VW454DefectReportedEvent) event;
        assertEquals("defect-1", reportedEvent.aggregateId());
        assertEquals("defect-1", reportedEvent.defectId());
        assertEquals("VW-454 — GitHub URL missing", reportedEvent.title());
        assertEquals("LOW", reportedEvent.severity());
        assertNotNull(reportedEvent.occurredAt());
    }

    @Test
    void shouldRejectUnknownCommand() {
        // Given
        var aggregate = new DefectAggregate("defect-1");
        var unknownCmd = new Object() implements com.example.domain.shared.Command {};

        // When & Then
        assertThrows(UnknownCommandException.class, () -> aggregate.execute(unknownCmd));
    }
}

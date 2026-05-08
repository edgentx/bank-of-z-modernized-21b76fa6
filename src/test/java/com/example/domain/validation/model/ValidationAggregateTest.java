package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.model.ReportDefectCommand;
import com.example.domain.validation.model.DefectReportedEvent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * TDD Red Phase: Unit tests for ValidationAggregate.
 * We expect these to fail until the aggregate and events are implemented.
 */
class ValidationAggregateTest {

    @Test
    void whenReportingDefect_shouldEmitEventContainingUrl() {
        // Arrange
        String defectId = "VW-454";
        String summary = "GitHub URL missing in Slack body";
        String githubUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";
        ValidationAggregate aggregate = new ValidationAggregate(defectId);
        ReportDefectCommand cmd = new ReportDefectCommand(defectId, summary, githubUrl);

        // Act
        List<DomainEvent> events = aggregate.execute(cmd);

        // Assert
        assertEquals(1, events.size(), "Should emit exactly one event");
        
        // We assume the event type is named DefectReportedEvent based on the error logs context
        assertTrue(events.get(0) instanceof DefectReportedEvent, "Event should be DefectReportedEvent");
        
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertEquals(defectId, event.aggregateId(), "Aggregate ID should match");
        // Verify the critical fix: the URL must be in the event payload
        assertEquals(githubUrl, event.githubUrl(), "GitHub URL must be captured in the event");
    }

    @Test
    void whenReportingDefect_withNullUrl_shouldFail() {
        // Arrange
        ValidationAggregate aggregate = new ValidationAggregate("VW-001");
        ReportDefectCommand cmd = new ReportDefectCommand("VW-001", "Summary", null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd), "Should reject null URL");
    }
}
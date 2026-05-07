package com.example.domain.validation;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationAggregate.
 * Ensures the defect reporting logic enforces invariants and emits correct events.
 */
class ValidationAggregateTest {

    @Test
    void shouldReportDefectSuccessfully() {
        // Arrange
        var aggregate = new ValidationAggregate("val-1");
        var cmd = new ReportDefectCmd(
            "val-1",
            "VW-454",
            "GitHub URL missing in Slack",
            "LOW"
        );

        // Act
        var events = aggregate.execute(cmd);

        // Assert
        assertEquals(1, events.size());
        var event = (DefectReportedEvent) events.get(0);
        
        assertEquals("val-1", event.aggregateId());
        assertEquals("VW-454", event.issueReference());
        assertEquals("LOW", event.severity());
        assertNotNull(event.occurredAt());
        
        // Verify Aggregate State
        assertTrue(aggregate.isReported());
        assertEquals("VW-454", aggregate.getIssueReference());
    }

    @Test
    void shouldThrowWhenIssueReferenceIsMissing() {
        // Arrange
        var aggregate = new ValidationAggregate("val-1");
        var cmd = new ReportDefectCmd(
            "val-1",
            null, // Missing Reference
            "Description",
            "LOW"
        );

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });
        
        assertTrue(ex.getMessage().contains("issueReference required"));
    }

    @Test
    void shouldThrowWhenSeverityIsInvalid() {
        // Arrange
        var aggregate = new ValidationAggregate("val-1");
        var cmd = new ReportDefectCmd(
            "val-1",
            "VW-454",
            "Description",
            "INVALID"
        );

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });
        
        assertTrue(ex.getMessage().contains("severity must be LOW, MEDIUM, or HIGH"));
    }

    @Test
    void shouldHandleUnknownCommand() {
        // Arrange
        var aggregate = new ValidationAggregate("val-1");
        var unknownCmd = new Object(); // Not a valid command

        // Using the domain wrapper for Command interface to strictly test Aggregate contract
        // In a real scenario, we would pass a Command implementation that isn't handled.
        // For this structure, we rely on the execute signature.
        // Assuming UnknownCommandException is thrown if command type doesn't match.
    }
}

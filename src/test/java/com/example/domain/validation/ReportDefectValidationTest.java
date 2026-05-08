package com.example.domain.validation;

import com.example.domain.shared.Command;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ReportDefectValidatedEvent;
import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ReportDefectAggregate.
 * Scope: Unit tests for the Aggregate logic.
 */
class ReportDefectValidationTest {

    @Test
    void shouldValidateBasicCommand() {
        // Given
        String defectId = "defect-123";
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId, 
            "S-FB-1", 
            "Validation failure", 
            "HIGH", 
            "validation"
        );
        ReportDefectAggregate aggregate = new ReportDefectAggregate(defectId);

        // When
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertFalse(events.isEmpty(), "Should produce an event");
        assertTrue(events.get(0) instanceof ReportDefectValidatedEvent, "Should produce ValidatedEvent");
        
        ReportDefectValidatedEvent event = (ReportDefectValidatedEvent) events.get(0);
        assertEquals(defectId, event.aggregateId());
        assertEquals("S-FB-1", event.storyId());
    }

    @Test
    void shouldRejectInvalidStoryId() {
        // Given
        String defectId = "defect-124";
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId, 
            "", // Empty Story ID
            "Validation failure", 
            "HIGH", 
            "validation"
        );
        ReportDefectAggregate aggregate = new ReportDefectAggregate(defectId);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> aggregate.execute(cmd)
        );
        assertTrue(exception.getMessage().contains("storyId"));
    }

    @Test
    void shouldRejectUnknownCommand() {
        // Given
        String defectId = "defect-125";
        Command unknownCmd = new Command() {}; // Anonymous invalid command
        ReportDefectAggregate aggregate = new ReportDefectAggregate(defectId);

        // When & Then
        assertThrows(UnknownCommandException.class, () -> aggregate.execute(unknownCmd));
    }
}

package com.example.domain.validation;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.model.ValidationReportedEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationAggregateTest {

    @Test
    void shouldCreateValidationReportedEventWhenDefectReported() {
        // Given
        String id = "v-force-360-1";
        ValidationAggregate aggregate = new ValidationAggregate(id);
        ReportDefectCmd cmd = new ReportDefectCmd(id, "Calculation mismatch", "HIGH");

        // When
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof ValidationReportedEvent);
        
        ValidationReportedEvent event = (ValidationReportedEvent) events.get(0);
        assertEquals(id, event.aggregateId());
        assertEquals("Calculation mismatch", event.summary());
        assertNotNull(event.occurredAt());
    }

    @Test
    void shouldThrowExceptionWhenSummaryIsBlank() {
        // Given
        String id = "v-force-360-2";
        ValidationAggregate aggregate = new ValidationAggregate(id);
        ReportDefectCmd cmd = new ReportDefectCmd(id, "", "LOW");

        // Then
        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }
}
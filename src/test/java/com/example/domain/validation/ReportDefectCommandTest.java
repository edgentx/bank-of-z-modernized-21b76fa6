package com.example.domain.validation;

import com.example.domain.shared.Aggregate;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReportDefectCommandTest {

    @Test
    void shouldRejectCommandWithNullProjectId() {
        // Given
        Aggregate aggregate = new ValidationAggregate();
        ReportDefectCmd cmd = new ReportDefectCmd(null, "Title", "Description", "LOW", "validation");

        // When & Then
        Exception ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("projectId required"));
    }

    @Test
    void shouldRejectCommandWithBlankProjectId() {
        // Given
        Aggregate aggregate = new ValidationAggregate();
        ReportDefectCmd cmd = new ReportDefectCmd("   ", "Title", "Description", "LOW", "validation");

        // When & Then
        Exception ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("projectId required"));
    }

    @Test
    void shouldRejectCommandWithNullTitle() {
        // Given
        Aggregate aggregate = new ValidationAggregate();
        ReportDefectCmd cmd = new ReportDefectCmd("pid", null, "Description", "LOW", "validation");

        // When & Then
        Exception ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("title required"));
    }

    @Test
    void shouldRejectCommandWithNullSeverity() {
        // Given
        Aggregate aggregate = new ValidationAggregate();
        ReportDefectCmd cmd = new ReportDefectCmd("pid", "Title", "Description", null, "validation");

        // When & Then
        Exception ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("severity required"));
    }

    @Test
    void shouldEmitDefectReportedEventWhenCommandIsValid() {
        // Given
        ValidationAggregate aggregate = new ValidationAggregate();
        ReportDefectCmd cmd = new ReportDefectCmd("21b76fa6-afb6-4593-9e1b-b5d7548ac4d1", "Fix: Validating VW-454", "Description", "LOW", "validation");

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof DefectReportedEvent);

        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertEquals("21b76fa6-afb6-4593-9e1b-b5d7548ac4d1", event.aggregateId());
        assertEquals("Fix: Validating VW-454", event.title());
    }
}

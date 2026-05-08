package com.example.domain.validation;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationAggregate.
 * Covers regression for S-FB-1.
 */
class ValidationAggregateTest {

    @Test
    void shouldCreateDefectReportedEvent() {
        ValidationAggregate aggregate = new ValidationAggregate("VW-454-AGG");
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "GitHub URL in Slack body",
            "LOW",
            null
        );

        List events = aggregate.execute(cmd);

        assertFalse(events.isEmpty());
        assertTrue(events.get(0) instanceof DefectReportedEvent);

        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertEquals("VW-454", event.defectId());
        assertEquals("https://github.com/issues/VW-454", event.githubUrl());
        assertTrue(aggregate.uncommittedEvents().contains(event));
    }

    @Test
    void shouldRejectDuplicateDefectReports() {
        ValidationAggregate aggregate = new ValidationAggregate("VW-454-AGG");
        ReportDefectCmd cmd = new ReportDefectCmd("VW-454", "Title", "LOW", null);
        
        aggregate.execute(cmd);
        
        Exception ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
        
        assertTrue(ex.getMessage().contains("already reported"));
    }

    @Test
    void shouldRejectUnknownCommands() {
        ValidationAggregate aggregate = new ValidationAggregate("VW-454-AGG");
        
        // Passing a dummy object that doesn't match known commands
        Exception ex = assertThrows(UnknownCommandException.class, () -> {
            aggregate.execute(new Object());
        });
    }
}
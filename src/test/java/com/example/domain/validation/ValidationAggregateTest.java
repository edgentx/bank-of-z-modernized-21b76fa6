package com.example.domain.validation;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.model.DefectReportedEvent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationAggregate.
 */
class ValidationAggregateTest {

    @Test
    void shouldReportDefectSuccessfully() {
        ValidationAggregate aggregate = new ValidationAggregate("VW-454");
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454", 
            "GitHub URL missing", 
            "https://github.com/bank-of-z/issues/454"
        );

        var events = aggregate.execute(cmd);

        assertEquals(1, events.size());
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertEquals("VW-454", event.aggregateId());
        assertEquals("GitHub URL missing", event.title());
        assertEquals("https://github.com/bank-of-z/issues/454", event.githubUrl());
    }

    @Test
    void shouldFailIfReportedTwice() {
        ValidationAggregate aggregate = new ValidationAggregate("VW-454");
        ReportDefectCmd cmd = new ReportDefectCmd("VW-454", "Title", "http://url");
        aggregate.execute(cmd);

        assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
    }

    @Test
    void shouldFailIfUrlIsMissing() {
        ValidationAggregate aggregate = new ValidationAggregate("VW-454");
        ReportDefectCmd cmd = new ReportDefectCmd("VW-454", "Title", ""); // Blank URL

        assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });
    }

    @Test
    void shouldThrowUnknownCommandForInvalidCmd() {
        ValidationAggregate aggregate = new ValidationAggregate("VW-454");
        Object invalidCmd = new Object();

        assertThrows(UnknownCommandException.class, () -> {
            aggregate.execute((com.example.domain.shared.Command) invalidCmd);
        });
    }
}
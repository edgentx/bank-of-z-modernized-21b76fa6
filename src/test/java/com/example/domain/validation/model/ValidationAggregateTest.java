package com.example.domain.validation.model;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.validation.model.command.ReportDefectCmd;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import java.util.List;

public class ValidationAggregateTest {

    @Test
    public void testReportDefectGeneratesEventWithUrl() {
        // Given
        String aggregateId = "vw-454";
        ValidationAggregate aggregate = new ValidationAggregate(aggregateId);
        String githubUrl = "https://github.com/bank-of-z/issues/454";
        ReportDefectCmd cmd = new ReportDefectCmd(aggregateId, githubUrl, "LOW", "validation");

        // When
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof DefectReportedEvent);
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertEquals(aggregateId, event.aggregateId());
        assertEquals(githubUrl, event.githubUrl());
        assertNotNull(event.occurredAt());
    }

    @Test
    public void testReportDefectRequiresValidUrl() {
        // Given
        String aggregateId = "vw-455";
        ValidationAggregate aggregate = new ValidationAggregate(aggregateId);
        ReportDefectCmd cmd = new ReportDefectCmd(aggregateId, "not-a-url", "LOW", "validation");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });
    }

    @Test
    public void testRejectsUnknownCommand() {
        // Given
        String aggregateId = "vw-999";
        ValidationAggregate aggregate = new ValidationAggregate(aggregateId);
        String unknownCmd = new String("Unknown Command Object"); // Simulating a wrong type

        // We need a Command instance, creating a dummy anonymous class for the test
        com.example.domain.shared.Command cmd = new com.example.domain.shared.Command() {};

        // When & Then
        assertThrows(UnknownCommandException.class, () -> {
            aggregate.execute(cmd);
        });
    }
}

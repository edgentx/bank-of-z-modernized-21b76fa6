package com.example.domain.validation;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.model.DefectReportedEvent;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationAggregateTest {

    @Test
    void whenReportDefectCommandReceived_shouldEmitEventWithGitHubUrl() {
        // Given
        String aggregateId = "vw-454";
        ValidationAggregate aggregate = new ValidationAggregate(aggregateId);
        ReportDefectCmd cmd = new ReportDefectCmd(aggregateId, "GitHub URL is missing from body", "https://github.com/egdcrypto/bank-of-z/issues/454");

        // When
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertFalse(events.isEmpty(), "Aggregate should emit an event");
        assertTrue(events.get(0) instanceof DefectReportedEvent, "Event should be DefectReportedEvent");
        
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertEquals("GitHub URL is missing from body", event.description());
        
        // Critical Assertion for S-FB-1
        assertNotNull(event.githubIssueUrl(), "GitHub Issue URL must be present in the event payload");
        assertEquals("https://github.com/egdcrypto/bank-of-z/issues/454", event.githubIssueUrl());
    }

    @Test
    void whenReportDefectCommandWithNullUrl_shouldThrowException() {
        // Given
        String aggregateId = "vw-454";
        ValidationAggregate aggregate = new ValidationAggregate(aggregateId);
        ReportDefectCmd cmd = new ReportDefectCmd(aggregateId, "Missing URL", null);

        // When / Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("githubIssueUrl"));
    }

    @Test
    void whenUnknownCommand_shouldThrowUnknownCommandException() {
        // Given
        String aggregateId = "vw-454";
        ValidationAggregate aggregate = new ValidationAggregate(aggregateId);
        Command unknownCmd = new Command() {}; // Anonymous mock command

        // When / Then
        assertThrows(UnknownCommandException.class, () -> aggregate.execute(unknownCmd));
    }
}

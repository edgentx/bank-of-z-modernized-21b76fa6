package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCommand;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the Defect Reporting Aggregate.
 * This validates the domain logic for reporting defects.
 */
class DefectReportingAggregateTest {

    @Test
    void shouldCreateDefectReportedEventWhenCommandIsValid() {
        // Given
        var aggregate = new DefectAggregate("defect-1");
        var cmd = new ReportDefectCommand(
            "defect-1",
            "VW-454",
            "https://github.com/bank-of-z/vforce360/issues/454",
            "Slack body missing URL"
        );

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertThat(events).hasSize(1);
        var event = (DefectReportedEvent) events.get(0);
        assertThat(event.type()).isEqualTo("DefectReportedEvent");
        assertThat(event.aggregateId()).isEqualTo("defect-1");
        assertThat(event.getIssueId()).isEqualTo("VW-454");
        assertThat(event.getGithubUrl()).isEqualTo("https://github.com/bank-of-z/vforce360/issues/454");
        assertThat(event.getSummary()).isEqualTo("Slack body missing URL");
        assertThat(event.occurredAt()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenSummaryIsEmpty() {
        // Given
        var aggregate = new DefectAggregate("defect-2");
        var cmd = new ReportDefectCommand(
            "defect-2",
            "VW-100",
            "http://github.com/...",
            "   " // Blank summary
        );

        // When & Then
        var ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertThat(ex.getMessage()).contains("summary required");
    }

    @Test
    void shouldThrowExceptionWhenGithubUrlIsInvalid() {
        // Given
        var aggregate = new DefectAggregate("defect-3");
        var cmd = new ReportDefectCommand(
            "defect-3",
            "VW-100",
            "not-a-url",
            "Summary"
        );

        // When & Then
        var ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertThat(ex.getMessage()).contains("valid GitHub URL required");
    }

    @Test
    void shouldThrowUnknownCommandForUnsupportedCommand() {
        // Given
        var aggregate = new DefectAggregate("defect-4");
        var unsupportedCmd = new Object(); // Not a ReportDefectCommand

        // This test structure mimics the AggregateRoot pattern which accepts generic Command objects
        // We cast to Command interface to match the signature
        var cmd = (com.example.domain.shared.Command) unsupportedCmd;

        // When & Then
        assertThrows(UnknownCommandException.class, () -> aggregate.execute(cmd));
    }
}

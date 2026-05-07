package com.example.domain.defect;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.DomainEvent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for DefectAggregate.
 * Covers standard command execution and specific VW-454 validation logic.
 */
class DefectAggregateTest {

    @Test
    void shouldReportDefectSuccessfully() {
        // Given
        var aggregate = new DefectAggregate("S-FB-1");
        var cmd = new ReportDefectCmd("S-FB-1", "GitHub URL missing", "Link is not in body", Map.of("severity", "LOW"));

        // When
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(DefectReportedEvent.class);

        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertThat(event.defectId()).isEqualTo("S-FB-1");
        assertThat(event.title()).isEqualTo("GitHub URL missing");
        
        // VW-454 Validation: The event MUST contain a non-null, non-blank GitHub URL
        assertThat(event.githubIssueUrl()).isNotBlank();
        assertThat(event.githubIssueUrl()).startsWith("https://github.com/");
        assertThat(event.githubIssueUrl()).contains("S-FB-1");
    }

    @Test
    void shouldPreventDuplicateReporting() {
        // Given
        var aggregate = new DefectAggregate("S-FB-1");
        var cmd = new ReportDefectCmd("S-FB-1", "Dup", "Desc", Map.of());
        aggregate.execute(cmd); // First time

        // When/Then
        assertThatThrownBy(() -> aggregate.execute(cmd))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already reported");
    }

    @Test
    void shouldRejectBlankTitle() {
        // Given
        var aggregate = new DefectAggregate("S-FB-1");
        var cmd = new ReportDefectCmd("S-FB-1", "   ", "Desc", Map.of());

        // When/Then
        assertThatThrownBy(() -> aggregate.execute(cmd))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

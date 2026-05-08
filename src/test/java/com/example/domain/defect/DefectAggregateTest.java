package com.example.domain.defect;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.DomainEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for DefectAggregate.
 * Validates the domain logic independent of external dependencies.
 */
class DefectAggregateTest {

    @Test
    void shouldCreateDefectReportedEventWhenCommandValid() {
        // Given
        var id = "defect-123";
        var aggregate = new DefectAggregate(id);
        var cmd = new ReportDefectCmd(
                id,
                "VW-454 Validation",
                "LOW",
                "validation",
                "GitHub URL missing from Slack body"
        );

        // When
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(DefectReportedEvent.class);

        var event = (DefectReportedEvent) events.get(0);
        assertThat(event.aggregateId()).isEqualTo(id);
        assertThat(event.severity()).isEqualTo("LOW");
        assertThat(event.gitHubIssueUrl()).isNotEmpty(); // The critical field for S-FB-1
    }

    @Test
    void shouldStoreGitHubUrlInAggregateState() {
        // Given
        var id = "defect-456";
        var aggregate = new DefectAggregate(id);
        var cmd = new ReportDefectCmd(id, "Test", "HIGH", "comp", "Desc");

        // When
        aggregate.execute(cmd);

        // Then
        assertThat(aggregate.getGitHubIssueUrl()).isNotNull();
        assertThat(aggregate.getGitHubIssueUrl()).contains("github.com");
    }

    @Test
    void shouldThrowExceptionForUnknownCommand() {
        // Given
        var aggregate = new DefectAggregate("x");
        var unknownCmd = new Object() {}; // Not a registered command type

        // Expectation (This test actually validates the framework wrapper usually, but here we check the aggregate logic)
        // Since execute takes Command interface, we need an instance of Command that isn't ReportDefectCmd.
        // We'll mock a Command implementation.
        Command badCmd = () -> "Unknown";

        assertThrows(com.example.domain.shared.UnknownCommandException.class, () -> {
            aggregate.execute(badCmd);
        });
    }
}

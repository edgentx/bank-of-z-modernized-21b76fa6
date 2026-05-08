package com.example.domain.defect;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase: Unit tests for the Defect Aggregate.
 * Story: S-FB-1
 */
class DefectAggregateTest {

    @Test
    void should_reject_report_command_when_summary_is_null() {
        // Arrange
        var defect = new DefectAggregate("defect-1");
        var cmd = new ReportDefectCmd(null, "Description", "LOW", "validation");

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> defect.execute(cmd));
        assertTrue(ex.getMessage().contains("summary required"));
    }

    @Test
    void should_reject_report_command_when_summary_is_blank() {
        var defect = new DefectAggregate("defect-1");
        var cmd = new ReportDefectCmd("   ", "Description", "LOW", "validation");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> defect.execute(cmd));
        assertTrue(ex.getMessage().contains("summary required"));
    }

    @Test
    void should_emit_event_on_valid_report() {
        // Arrange
        var defectId = "vw-454";
        var defect = new DefectAggregate(defectId);
        var cmd = new ReportDefectCmd("Validating VW-454", "URL missing in Slack", "LOW", "validation");

        // Act
        var events = defect.execute(cmd);

        // Assert
        assertEquals(1, events.size());
        var event = events.get(0);
        assertEquals("DefectReportedEvent", event.type());
        assertEquals(defectId, event.aggregateId());
        assertNotNull(event.occurredAt());
    }

    @Test
    void should_set_aggregate_state_on_report() {
        // This test ensures the aggregate state updates correctly so the
        // Workflow can access the URL for the Slack notification.
        var defect = new DefectAggregate("vw-454");
        var cmd = new ReportDefectCmd("GitHub URL missing", "Fix the Slack body", "LOW", "validation");

        defect.execute(cmd);

        // Assuming DefectAggregate will expose getters derived from the state
        // For the workflow to pass the URL to Slack.
        assertNotNull(defect.getSummary());
        assertNotNull(defect.getDescription());
        // We verify state mutation happens
    }
}
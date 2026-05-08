package com.example.domain.vforce360;

import com.example.domain.shared.DomainEvent;
import com.example.domain.vforce360.model.DefectAggregate;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for DefectAggregate.
 * Pure Java unit tests to ensure state transitions and event emission logic.
 */
class DefectAggregateTest {

    @Test
    void shouldExecuteReportDefectCommand() {
        // Arrange
        DefectAggregate aggregate = new DefectAggregate("VW-001");
        ReportDefectCmd cmd = new ReportDefectCmd(
                "VW-001",
                "Test Defect",
                "Description",
                ReportDefectCmd.Severity.HIGH,
                Map.of()
        );

        // Act
        List<DomainEvent> events = aggregate.execute(cmd);

        // Assert
        assertFalse(events.isEmpty());
        assertTrue(events.get(0) instanceof DefectReportedEvent);

        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertEquals("VW-001", event.defectId());
        assertNotNull(event.githubIssueUrl()); // Key validation for VW-454
        assertFalse(event.githubIssueUrl().isBlank());
    }

    @Test
    void shouldRejectDuplicateReportCommands() {
        // Arrange
        DefectAggregate aggregate = new DefectAggregate("VW-002");
        ReportDefectCmd cmd = new ReportDefectCmd(
                "VW-002", "Dup", "Desc", ReportDefectCmd.Severity.LOW, Map.of()
        );
        
        // Act - First run succeeds
        aggregate.execute(cmd);

        // Act - Second run fails
        Exception ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("already reported"));
    }
}
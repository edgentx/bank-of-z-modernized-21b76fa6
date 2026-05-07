package com.example.domain.defect;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Defect Aggregate domain logic.
 * Validates command handling and event generation state.
 */
class DefectReportedEventTest {

    @Test
    void shouldExecuteReportDefectCommand() {
        // Given
        var defectId = "DEFECT-101";
        var title = "VW-454 — GitHub URL in Slack body";
        var description = "Severity: LOW";
        var aggregate = new DefectAggregate(defectId);
        var cmd = new ReportDefectCmd(defectId, title, description);

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertNotNull(events);
        assertEquals(1, events.size());
        assertTrue(aggregate.isReported());
    }

    @Test
    void shouldThrowOnUnknownCommand() {
        // Given
        var aggregate = new DefectAggregate("DEFECT-102");
        var unknownCmd = new Object() {}; // Invalid command type

        // Expect
        assertThrows(UnknownCommandException.class, () -> aggregate.execute((com.example.domain.shared.Command) unknownCmd));
    }
}

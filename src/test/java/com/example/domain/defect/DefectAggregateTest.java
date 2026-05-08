package com.example.domain.defect;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefectAggregateTest {

    @Test
    void shouldReportDefectWhenValid() {
        // Given
        var defectId = "d-123";
        var aggregate = new DefectAggregate(defectId);
        var cmd = new ReportDefectCmd(defectId, "GH-454 Link broken", "LOW");

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof DefectReportedEvent);
        var event = (DefectReportedEvent) events.get(0);
        assertEquals("GH-454 Link broken", event.title());
        assertEquals(defectId, event.aggregateId());
    }

    @Test
    void shouldThrowWhenTitleBlank() {
        // Given
        var defectId = "d-456";
        var aggregate = new DefectAggregate(defectId);
        var cmd = new ReportDefectCmd(defectId, "  ", "LOW");

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }

    @Test
    void shouldThrowWhenAlreadyReported() {
        // Given
        var defectId = "d-789";
        var aggregate = new DefectAggregate(defectId);
        var cmd1 = new ReportDefectCmd(defectId, "First", "LOW");
        aggregate.execute(cmd1);
        var cmd2 = new ReportDefectCmd(defectId, "Second", "LOW");

        // When / Then
        assertThrows(IllegalStateException.class, () -> aggregate.execute(cmd2));
    }
}

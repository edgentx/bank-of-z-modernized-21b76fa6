package com.example.domain.vforce360;

import com.example.domain.shared.ReportDefectCmd;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.VForce360Aggregate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VForce360AggregateTest {

    @Test
    void shouldReportDefectSuccessfully() {
        // Given
        var aggregate = new VForce360Aggregate("vf-123");
        var cmd = new ReportDefectCmd(
            "defect-1",
            "VW-454 Validation",
            "GitHub URL missing",
            "validation",
            "LOW"
        );

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        var event = events.get(0);
        assertInstanceOf(DefectReportedEvent.class, event);
        
        var reportedEvent = (DefectReportedEvent) event;
        assertEquals("defect-1", reportedEvent.aggregateId());
        assertEquals("VW-454 Validation", reportedEvent.title());
        assertEquals("validation", reportedEvent.component());
        assertEquals("LOW", reportedEvent.severity());
    }

    @Test
    void shouldRejectReportWithBlankTitle() {
        // Given
        var aggregate = new VForce360Aggregate("vf-123");
        var cmd = new ReportDefectCmd("defect-2", "", "Description", "validation", "LOW");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }
}
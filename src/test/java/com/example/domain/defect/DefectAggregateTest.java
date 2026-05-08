package com.example.domain.defect;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefectAggregateTest {

    @Test
    void shouldReportDefectAndGenerateUrl() {
        // Arrange
        String defectId = "VW-454";
        DefectAggregate aggregate = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, "Validation Error", "Link is missing");

        // Act
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Assert
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof DefectReportedEvent);
        
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertEquals(defectId, event.defectId());
        assertEquals("Validation Error", event.title());
        assertNotNull(event.githubUrl());
        assertTrue(event.githubUrl().contains(defectId));
        assertEquals(event.githubUrl(), aggregate.getGithubUrl());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsBlank() {
        // Arrange
        DefectAggregate aggregate = new DefectAggregate("VW-454");
        ReportDefectCmd cmd = new ReportDefectCmd("VW-454", "   ", "Desc");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }
}

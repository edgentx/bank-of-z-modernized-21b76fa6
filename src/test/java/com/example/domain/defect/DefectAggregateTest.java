package com.example.domain.defect;

import com.example.defect.domain.DefectAggregate;
import com.example.defect.domain.DefectReportedEvent;
import com.example.defect.domain.ReportDefectCmd;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

/**
 * Unit tests for DefectAggregate.
 */
class DefectAggregateTest {

    @Test
    void testExecute_ReportDefect_Success() {
        // Arrange
        String defectId = "VW-454";
        DefectAggregate aggregate = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "GitHub URL Validation",
            "LOW",
            "validation",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1",
            Map.of("reporter", "VForce360")
        );

        // Act
        var events = aggregate.execute(cmd);

        // Assert
        assertFalse(events.isEmpty());
        assertTrue(events.get(0) instanceof DefectReportedEvent);
        
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertEquals(defectId, event.defectId());
        assertNotNull(event.githubUrl());
        assertTrue(event.githubUrl().contains(defectId));
        
        // Verify aggregate state
        assertTrue(aggregate.isReported());
        assertEquals(event.githubUrl(), aggregate.getGithubUrl());
    }

    @Test
    void testExecute_ReportDefect_ThrowsOnMissingProjectId() {
        // Arrange
        String defectId = "VW-455";
        DefectAggregate aggregate = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId, "Title", "LOW", "comp", null, Map.of()
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }

    @Test
    void testExecute_ReportDefect_ThrowsOnDuplicateReport() {
        // Arrange
        String defectId = "VW-456";
        DefectAggregate aggregate = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId, "Title", "LOW", "comp", "proj-1", Map.of()
        );
        aggregate.execute(cmd); // First report

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> aggregate.execute(cmd));
    }

    @Test
    void testExecute_UnknownCommand() {
        // Arrange
        String defectId = "VW-457";
        DefectAggregate aggregate = new DefectAggregate(defectId);
        
        class UnknownCommand implements com.example.domain.shared.Command {}

        // Act & Assert
        assertThrows(UnknownCommandException.class, () -> aggregate.execute(new UnknownCommand()));
    }
}

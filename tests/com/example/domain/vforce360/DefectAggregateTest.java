package com.example.domain.vforce360;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.vforce360.model.DefectAggregate;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DefectAggregate.
 * Covers basic state transitions and event generation.
 */
class DefectAggregateTest {

    @Test
    void testReportDefectGeneratesEventWithUrl() {
        // Arrange
        String defectId = "VW-454";
        DefectAggregate aggregate = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId, 
            "GitHub URL in Slack body", 
            "Validation failed", 
            "LOW"
        );

        // Act
        var events = aggregate.execute(cmd);

        // Assert
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof DefectReportedEvent);
        
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertEquals("VW-454", event.defectId());
        
        // CRITICAL CHECK: Ensure URL is present (mirroring expected behavior)
        assertNotNull(event.githubIssueUrl(), "GitHub Issue URL must be present in the event");
        assertTrue(event.githubIssueUrl().contains("github.com"), "URL should point to GitHub");
    }

    @Test
    void testCannotReportSameDefectTwice() {
        // Arrange
        String defectId = "VW-454";
        DefectAggregate aggregate = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, "Title", "Desc", "HIGH");
        aggregate.execute(cmd); // First report

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd); // Second report
        });
    }
}

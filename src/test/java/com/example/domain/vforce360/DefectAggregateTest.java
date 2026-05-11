package com.example.domain.vforce360;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.vforce360.model.DefectAggregate;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DefectAggregate.
 * Ensures that reporting a defect generates the correct event with a GitHub URL.
 */
class DefectAggregateTest {

    @Test
    void whenReportDefectCommandReceived_thenEventContainsGitHubUrl() {
        // Arrange
        String defectId = "VW-454";
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "Fix: Validating VW-454",
            "GitHub URL in Slack body",
            projectId
        );
        DefectAggregate aggregate = new DefectAggregate(defectId);

        // Act
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Assert
        assertEquals(1, events.size(), "Should emit exactly one event");
        
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertNotNull(event.githubUrl(), "GitHub URL must be present in the event");
        assertTrue(event.githubUrl().contains(defectId), "GitHub URL should contain the defect ID");
        assertEquals(defectId, event.aggregateId());
    }

    @Test
    void whenReportingSameDefectTwice_thenThrowIllegalStateException() {
        // Arrange
        String defectId = "VW-454";
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, "Title", "Desc", "PID");
        DefectAggregate aggregate = new DefectAggregate(defectId);
        aggregate.execute(cmd); // First report

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd); // Second report
        });
    }

    @Test
    void whenUnknownCommandReceived_thenThrowUnknownCommandException() {
        // Arrange
        DefectAggregate aggregate = new DefectAggregate("ID");
        Command unknownCmd = new Command() {}; // Anonymous implementation

        // Act & Assert
        assertThrows(UnknownCommandException.class, () -> {
            aggregate.execute(unknownCmd);
        });
    }
}

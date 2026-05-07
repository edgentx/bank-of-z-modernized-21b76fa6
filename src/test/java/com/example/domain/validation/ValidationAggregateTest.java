package com.example.domain.validation;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.model.ValidationReportedEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationAggregate.
 * Story: S-FB-1 - Fix: Validating VW-454 (GitHub URL in Slack body)
 */
class ValidationAggregateTest {

    @Test
    void testReportDefect_generatesEventWithValidUrl() {
        // Arrange
        String defectId = "VW-454";
        String description = "GitHub URL in Slack body";
        ValidationAggregate aggregate = new ValidationAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, description);

        // Act
        var events = aggregate.execute(cmd);

        // Assert
        assertEquals(1, events.size());
        ValidationReportedEvent event = (ValidationReportedEvent) events.get(0);
        
        assertNotNull(event.slackBody());
        String body = event.slackBody();
        
        // Validate URL presence (Expected Behavior)
        assertTrue(body.contains("http"), "Slack body should contain a protocol (http/https)");
        assertTrue(body.contains(defectId), "Slack body should contain the Defect ID");
        assertTrue(body.contains("github"), "Slack body should reference GitHub");
    }

    @Test
    void testReportDefect_throwsOnNullDefectId() {
        // Arrange
        ValidationAggregate aggregate = new ValidationAggregate("any-id");
        ReportDefectCmd cmd = new ReportDefectCmd(null, "Some description");

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("defectId"));
    }

    @Test
    void testReportDefect_throwsOnBlankDescription() {
        // Arrange
        ValidationAggregate aggregate = new ValidationAggregate("any-id");
        ReportDefectCmd cmd = new ReportDefectCmd("VW-455", "   ");

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("description"));
    }
}

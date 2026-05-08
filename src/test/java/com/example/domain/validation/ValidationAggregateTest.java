package com.example.domain.validation;

import com.example.domain.validation.model.DefectReportedEvent;
com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationAggregate.
 * Covers the logic of defect reporting and event emission.
 */
class ValidationAggregateTest {

    @Test
    void testReportDefectGeneratesGitHubUrl() {
        // Given
        var aggregate = new ValidationAggregate("v-force-360");
        var cmd = new ReportDefectCmd("v-force-360", "Test Description", "LOW");

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertFalse(events.isEmpty(), "Should emit an event");
        var event = events.get(0);
        assertInstanceOf(DefectReportedEvent.class, event);
        
        var defectEvent = (DefectReportedEvent) event;
        assertNotNull(defectEvent.issueUrl(), "Issue URL must not be null");
        assertTrue(defectEvent.issueUrl().startsWith("https://github.com"), 
            "Issue URL must be a GitHub link");
        assertTrue(defectEvent.issueUrl().contains("v-force-360"), 
            "Issue URL must contain the aggregate ID");
    }

    @Test
    void testReportDefectRequiresDescription() {
        // Given
        var aggregate = new ValidationAggregate("v-force-360");
        var cmd = new ReportDefectCmd("v-force-360", "", "LOW");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd),
            "Should throw on blank description");
    }
}

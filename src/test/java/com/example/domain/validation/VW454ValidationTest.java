package com.example.domain.validation;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Validation Aggregate.
 * Tests the domain logic in isolation before wiring up the Slack notification.
 */
class VW454ValidationTest {

    @Test
    void shouldGenerateDefectReportedEventWithValidUrl() {
        // Arrange
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/bank-of-z/issues/454";
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "GitHub URL in Slack body",
            "LOW",
            expectedUrl
        );

        ValidationAggregate aggregate = new ValidationAggregate(defectId);

        // Act
        var events = aggregate.execute(cmd);

        // Assert
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof DefectReportedEvent);

        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertEquals(defectId, event.defectId());
        assertEquals(expectedUrl, event.url());
        assertEquals("LOW", event.severity());
    }

    @Test
    void shouldThrowExceptionWhenUrlIsMissing() {
        // Arrange
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-455",
            "Missing URL",
            "HIGH",
            null // Invalid URL
        );

        ValidationAggregate aggregate = new ValidationAggregate("VW-455");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }
}
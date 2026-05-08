package com.example.domain.validation;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.validation.model.DefectReporterAggregate;
import com.example.domain.validation.model.ReportDefectCmd;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase: Unit tests for DefectReporterAggregate.
 * Validating VW-454 — GitHub URL in Slack body.
 */
class DefectReporterAggregateTest {

    @Test
    void whenReportDefectCommandIsValid_thenExpectGitHubUrlInEventBody() {
        // Arrange
        String defectId = "VW-454";
        String severity = "LOW";
        String component = "validation";
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String expectedUrl = "https://github.com/bank-of-z/issues/" + defectId;
        
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId, 
            severity, 
            component, 
            projectId, 
            "Description text"
        );

        DefectReporterAggregate aggregate = new DefectReporterAggregate(defectId);

        // Act
        List<DomainEvent> events = aggregate.execute(cmd);

        // Assert
        assertEquals(1, events.size(), "Should produce exactly one event");
        
        DomainEvent event = events.get(0);
        assertEquals("DefectReportedEvent", event.type());
        assertEquals(defectId, event.aggregateId());
        assertNotNull(event.occurredAt());

        // CRITICAL ASSERTION: The body must contain the GitHub issue URL
        // This verifies the fix for the reported defect
        String slackBody = event.body();
        assertNotNull(slackBody, "Slack body should not be null");
        assertTrue(
            slackBody.contains(expectedUrl), 
            "Slack body must contain the GitHub issue URL. Expected: " + expectedUrl + " in body: " + slackBody
        );
    }

    @Test
    void whenCommandIsUnknown_thenThrowUnknownCommandException() {
        // Arrange
        String defectId = "VW-999";
        DefectReporterAggregate aggregate = new DefectReporterAggregate(defectId);
        
        Object invalidCmd = new Object(); // Not a valid command

        // Act & Assert
        // Note: AggregateRoot pattern usually expects specific Command interfaces.
        // Passing a raw Object might fail at runtime or immediately depending on implementation.
        // Here we test the UnknownCommandException handling.
        assertThrows(UnknownCommandException.class, () -> {
            aggregate.execute((com.example.domain.shared.Command) invalidCmd);
        });
    }
}
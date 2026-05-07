package com.example.domain.vforce;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.vforce.model.DefectReportingAggregate;
import com.example.domain.vforce.model.ReportDefectCmd;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Defect Reporting aggregate.
 * Focuses on the 'red' state of TDD: asserting expected behavior exists.
 */
class DefectReportingAggregateTest {

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new DefectReportingAggregate(null));
    }

    @Test
    void shouldThrowExceptionWhenIdIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new DefectReportingAggregate("   "));
    }

    @Test
    void shouldInitializeWithCorrectId() {
        DefectReportingAggregate aggregate = new DefectReportingAggregate("issue-123");
        assertEquals("issue-123", aggregate.id());
    }

    @Test
    void shouldHandleUnknownCommand() {
        DefectReportingAggregate aggregate = new DefectReportingAggregate("issue-123");
        Object unknownCmd = new Object(); // Not a recognized command

        assertThrows(UnknownCommandException.class, () -> aggregate.execute(unknownCmd));
    }

    @Test
    void shouldFailIfTitleIsMissing() {
        DefectReportingAggregate aggregate = new DefectReportingAggregate("issue-123");
        ReportDefectCmd cmd = new ReportDefectCmd("issue-123", null, "Description", "HIGH", "BUG");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("title"));
    }

    @Test
    void shouldFailIfDescriptionIsMissing() {
        DefectReportingAggregate aggregate = new DefectReportingAggregate("issue-123");
        ReportDefectCmd cmd = new ReportDefectCmd("issue-123", "Title", null, "HIGH", "BUG");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("description"));
    }

    @Test
    void shouldReportDefectSuccessfully() {
        // Given
        String issueId = "VW-454";
        String title = "GitHub URL in Slack body";
        String description = "Verify the link is present in the body.";
        DefectReportingAggregate aggregate = new DefectReportingAggregate(issueId);
        ReportDefectCmd cmd = new ReportDefectCmd(issueId, title, description, "LOW", "DEFECT");

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertNotNull(events);
        assertFalse(events.isEmpty());
        
        // Verify the specific event content for VW-454 requirements
        var event = events.get(0);
        assertEquals("DefectReportedEvent", event.type());
        assertEquals(issueId, event.aggregateId());
        assertNotNull(event.occurredAt());
    }
}

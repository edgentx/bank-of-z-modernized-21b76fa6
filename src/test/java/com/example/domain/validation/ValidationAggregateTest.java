package com.example.domain.validation;

import com.example.domain.validation.model.*;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationAggregate.
 * Story: S-FB-1
 * TDD Phase: RED (Failing tests against unimplemented logic)
 */
class ValidationAggregateTest {

    @Test
    void should_throw_when_command_unknown() {
        ValidationAggregate aggregate = new ValidationAggregate("val-1");
        Object unknownCmd = new Object(); // Not a known command
        
        assertThrows(UnknownCommandException.class, () -> aggregate.execute(unknownCmd));
    }

    @Test
    void should_throw_when_report_defect_missing_severity() {
        ValidationAggregate aggregate = new ValidationAggregate("val-1");
        ReportDefectCmd cmd = new ReportDefectCmd("val-1", "Defect Title", null, "Component", Instant.now());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("severity"));
    }

    @Test
    void should_create_report_defect_event() {
        ValidationAggregate aggregate = new ValidationAggregate("val-1");
        ReportDefectCmd cmd = new ReportDefectCmd("val-1", "GitHub link missing", Severity.LOW, "validation", Instant.now());

        List<DomainEvent> events = aggregate.execute(cmd);

        assertFalse(events.isEmpty());
        assertTrue(events.get(0) instanceof DefectReportedEvent);

        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertEquals("val-1", event.aggregateId());
        assertEquals("GitHub link missing", event.title());
        assertEquals(Severity.LOW, event.severity());
    }

    @Test
    void should_map_issue_url_to_github_event() {
        ValidationAggregate aggregate = new ValidationAggregate("val-1");
        MapIssueUrlCmd cmd = new MapIssueUrlCmd("val-1", "https://github.com/example/issues/1");

        List<DomainEvent> events = aggregate.execute(cmd);

        assertTrue(events.get(0) instanceof IssueUrlMappedEvent);
        IssueUrlMappedEvent event = (IssueUrlMappedEvent) events.get(0);
        assertEquals("https://github.com/example/issues/1", event.url());
    }
}
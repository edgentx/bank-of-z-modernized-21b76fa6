package com.example.domain.validation;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.validation.ValidationAggregate;
import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Validation Aggregate.
 * Verifies command handling and event generation logic.
 */
class ValidationAggregateTest {

    @Test
    void shouldThrowUnknownCommandForUnsupportedCommand() {
        var aggregate = new ValidationAggregate("test-id");
        Object unsupportedCmd = new Object();

        Exception exception = assertThrows(UnknownCommandException.class, () -> {
            aggregate.execute((com.example.domain.shared.Command) unsupportedCmd);
        });

        assertTrue(exception.getMessage().contains("Unknown command"));
    }

    @Test
    void shouldThrowExceptionWhenReportingDuplicateDefect() {
        var aggregate = new ValidationAggregate("test-id");
        var cmd = new ReportDefectCmd("test-id", "Title", "LOW", "proj-id");
        
        // First report succeeds
        aggregate.execute(cmd);
        
        // Second report fails
        assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
    }

    @Test
    void shouldGenerateGithubUrlFormatUponReportingDefect() {
        var aggregate = new ValidationAggregate("vw-454");
        var cmd = new ReportDefectCmd("vw-454", "Defect Title", "LOW", "21b76fa6");

        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        assertEquals(1, events.size());
        
        var event = (DefectReportedEvent) events.get(0);
        
        // RED PHASE: Validation Logic
        // Acceptance Criteria: Slack body includes GitHub issue: <url>
        // We assume the URL must be non-null, start with http, and contain the issue ID or reference.
        assertNotNull(event.githubUrl(), "GitHub URL must not be null");
        assertTrue(event.githubUrl().startsWith("http"), "GitHub URL must start with http");
        
        // Specific check for defect ID inclusion to ensure it's a valid link line
        // The stub currently returns "https://github.com/issues/vw-454".
        // A real implementation might fetch a real ID. This test ensures the behavior is captured.
        assertTrue(event.githubUrl().contains("vw-454"), "GitHub URL should contain the defect ID for tracking");
    }
}
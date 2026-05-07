package com.example.domain.validation;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationAggregate.
 * Covers happy path and validation errors.
 */
class ValidationAggregateTest {

    @Test
    void shouldReportDefectWithValidGitHubUrl() {
        // Given
        var aggr = new ValidationAggregate("val-1");
        var cmd = new ReportDefectCmd("val-1", "VW-454", "https://github.com/example/repo/issues/454");

        // When
        var events = aggr.execute(cmd);

        // Then
        assertEquals(1, events.size());
        assertTrue(aggr.isReported());
        assertEquals("https://github.com/example/repo/issues/454", aggr.getGithubIssueUrl());

        var event = (DefectReportedEvent) events.get(0);
        assertEquals("val-1", event.aggregateId());
        assertEquals("VW-454", event.title());
        assertEquals("https://github.com/example/repo/issues/454", event.githubIssueUrl());
    }

    @Test
    void shouldRejectInvalidGitHubUrl() {
        var aggr = new ValidationAggregate("val-1");
        var cmd = new ReportDefectCmd("val-1", "VW-454", "http://google.com");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> aggr.execute(cmd));
        assertTrue(ex.getMessage().contains("Valid GitHub Issue URL is required"));
    }

    @Test
    void shouldRejectEmptyGitHubUrl() {
        var aggr = new ValidationAggregate("val-1");
        var cmd = new ReportDefectCmd("val-1", "VW-454", "");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> aggr.execute(cmd));
        assertTrue(ex.getMessage().contains("Valid GitHub Issue URL is required"));
    }

    @Test
    void shouldThrowUnknownCommand() {
        var aggr = new ValidationAggregate("val-1");
        var cmd = new Object(); // Not a valid command

        // We need to wrap this in a Command implementation for the test
        class FakeCmd implements com.example.domain.shared.Command {}
        
        assertThrows(UnknownCommandException.class, () -> aggr.execute(new FakeCmd()));
    }
}

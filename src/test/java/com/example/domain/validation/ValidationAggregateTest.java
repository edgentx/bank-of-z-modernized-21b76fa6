package com.example.domain.validation;

import com.example.domain.validation.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationAggregateTest {

    @Test
    void shouldReportDefect() {
        ValidationAggregate aggregate = new ValidationAggregate("val-1");
        ReportDefectCmd cmd = new ReportDefectCmd("val-1", "Summary", "Desc", "LOW");

        var events = aggregate.execute(cmd);

        assertFalse(events.isEmpty());
        assertTrue(aggregate.isReported());
        assertEquals("Summary", aggregate.getSummary());
    }

    @Test
    void shouldLinkGitHubIssue() {
        ValidationAggregate aggregate = new ValidationAggregate("val-1");
        // Pre-condition: Report defect
        aggregate.execute(new ReportDefectCmd("val-1", "S", "D", "LOW"));
        
        String url = "https://github.com/bank-of-z/issues/123";
        var events = aggregate.execute(new LinkGitHubIssueCmd("val-1", url));

        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof GitHubIssueLinkedEvent);
        assertEquals(url, aggregate.getGithubIssueUrl());
    }

    @Test
    void shouldThrowExceptionWhenLinkingWithoutReporting() {
        ValidationAggregate aggregate = new ValidationAggregate("val-1");
        
        assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(new LinkGitHubIssueCmd("val-1", "http://github.com/..."));
        });
    }
}

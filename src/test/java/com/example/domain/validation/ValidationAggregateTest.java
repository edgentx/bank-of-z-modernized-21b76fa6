package com.example.domain.validation;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationAggregate.
 * Verifies business logic regarding defect reporting commands.
 */
class ValidationAggregateTest {

    @Test
    void shouldPublishEventWhenReportDefectCommandIsValid() {
        // Given
        String validationId = "v-force-360-1";
        ValidationAggregate aggregate = new ValidationAggregate(validationId);
        ReportDefectCmd cmd = new ReportDefectCmd(
            "S-FB-1",
            "GitHub URL missing",
            "URL is null",
            "LOW",
            "https://github.com/example/bank-of-z/issues/454"
        );

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        assertTrue(aggregate.isReported());

        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertEquals("https://github.com/example/bank-of-z/issues/454", event.githubIssueUrl());
    }

    @Test
    void shouldThrowExceptionWhenGitHubUrlIsMissing() {
        // Given
        ValidationAggregate aggregate = new ValidationAggregate("v-force-360-1");
        ReportDefectCmd cmd = new ReportDefectCmd(
            "S-FB-1",
            "GitHub URL missing",
            "URL is null",
            "LOW",
            null // URL is null
        );

        // When / Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("GitHub Issue URL is required"));
    }

    @Test
    void shouldThrowExceptionWhenGitHubUrlIsInvalidFormat() {
        // Given
        ValidationAggregate aggregate = new ValidationAggregate("v-force-360-1");
        ReportDefectCmd cmd = new ReportDefectCmd(
            "S-FB-1",
            "GitHub URL invalid",
            "Bad format",
            "LOW",
            "https://gitlab.com/example/issues/1"
        );

        // When / Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("Invalid GitHub URL format"));
    }
}
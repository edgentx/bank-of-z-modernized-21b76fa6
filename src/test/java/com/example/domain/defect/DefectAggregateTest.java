package com.example.domain.defect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DefectAggregate.
 * Focus: Validating VW-454 regression.
 */
class DefectAggregateTest {

    @Test
    void shouldThrowWhenGithubUrlIsMissing() {
        // Arrange
        var aggregate = new DefectAggregate("S-FB-1");
        var cmd = new ReportDefectCmd("S-FB-1", "VW-454", "Slack body missing URL", "");

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("GitHub URL is required"));
    }

    @Test
    void shouldThrowWhenGithubUrlIsInvalid() {
        // Arrange
        var aggregate = new DefectAggregate("S-FB-1");
        var cmd = new ReportDefectCmd("S-FB-1", "VW-454", "Bad URL", "https://gitlab.com/example/repo");

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("Invalid GitHub URL format"));
    }

    @Test
    void shouldEmitEventWhenGithubUrlIsValid() {
        // Arrange
        var aggregate = new DefectAggregate("S-FB-1");
        var validUrl = "https://github.com/example/bank-of-z/issues/454";
        var cmd = new ReportDefectCmd("S-FB-1", "VW-454", "Fix URL", validUrl);

        // Act
        var events = aggregate.execute(cmd);

        // Assert
        assertEquals(1, events.size());
        var event = (DefectReportedEvent) events.get(0);
        assertEquals(validUrl, event.githubUrl());
        assertEquals("DefectReported", event.type());
    }
}

package com.example.domain.notification;

import com.example.domain.notification.model.NotificationAggregate;
import com.example.domain.notification.model.NotificationPostedEvent;
import com.example.domain.notification.model.ReportDefectCommand;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NotificationAggregate.
 * Focuses on business logic for Slack body formatting (S-FB-1).
 */
class NotificationAggregateTest {

    @Test
    void shouldContainGitHubUrlWhenReportingDefect() {
        // Given
        var aggregate = new NotificationAggregate("test-id");
        var cmd = new ReportDefectCommand(
            "Test Defect",
            "Description",
            "HIGH",
            "api",
            Map.of("githubIssueId", "123")
        );

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        var event = (NotificationPostedEvent) events.get(0);
        
        // Critical assertion for S-FB-1: URL must be in body
        assertTrue(event.body().contains("https://github.com/example-org/egdcrypto-bank-of-z/issues/123"));
        assertEquals("slack", event.channel());
    }

    @Test
    void shouldFailValidationIfMetadataMissing() {
        // This test ensures robustness. If the implementation relies on metadata being present,
        // we verify behavior here. For S-FB-1, we assume happy path primarily,
        // but checking handling of missing metadata is good practice.
        var aggregate = new NotificationAggregate("test-id");
        var cmd = new ReportDefectCommand(
            "Test Defect",
            "Description",
            "HIGH",
            "api",
            Map.of() // Empty metadata
        );

        // Expect the system to handle missing ID gracefully (e.g., default to UNKNOWN)
        var events = aggregate.execute(cmd);
        var event = (NotificationPostedEvent) events.get(0);
        assertTrue(event.body().contains("UNKNOWN"));
    }
}

package com.example.domain.validation;

import com.example.domain.shared.DomainEvent;
import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationAggregate.
 * Focus: S-FB-1 (VW-454) - Verifying GitHub URL propagation.
 */
class ValidationAggregateTest {

    public static final String TEST_GITHUB_URL = "https://github.com/example/issues/454";

    @Test
    void shouldContainGitHubUrlInSlackBodyWhenReportingDefect() {
        // Given
        String defectId = "VW-454";
        String title = "Fix GitHub URL in Slack body";
        ValidationAggregate aggregate = new ValidationAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, title, "desc", TEST_GITHUB_URL, Map.of());

        // When
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof DefectReportedEvent);

        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertNotNull(event.slackBody(), "Slack body should not be null");
        
        // VW-454 Verification: Check for explicit GitHub URL string in body
        assertTrue(
            event.slackBody().contains(TEST_GITHUB_URL),
            "Slack body must contain the specific GitHub URL provided in command."
        );
        
        // Ensure generic format
        assertTrue(event.slackBody().contains("GitHub Issue:"), "Body should identify the GitHub link");
    }

    @Test
    void shouldThrowExceptionIfGitHubUrlIsMissing() {
        // Given
        ValidationAggregate aggregate = new ValidationAggregate("missing-url");
        ReportDefectCmd cmd = new ReportDefectCmd("missing-url", "Missing URL", "desc", "", Map.of());

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });
        assertTrue(ex.getMessage().contains("GitHub URL is required"));
    }
}

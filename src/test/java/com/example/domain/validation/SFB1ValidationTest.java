package com.example.domain.validation;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.model.DefectReportedEvent;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for Story S-FB-1.
 * Validates VW-454 — GitHub URL in Slack body (end-to-end).
 */
class SFB1ValidationTest {

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenReportingDefect() {
        // Arrange
        String aggregateId = "agg-123";
        ValidationAggregate aggregate = new ValidationAggregate(aggregateId);
        String expectedUrl = "https://github.com/bank-of-z/issues/454";
        
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454", 
            "GitHub URL missing in Slack", 
            "The Slack body does not contain the link.", 
            expectedUrl, 
            "LOW"
        );

        // Act
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Assert
        assertFalse(events.isEmpty(), "Should produce an event");
        assertTrue(events.get(0) instanceof DefectReportedEvent, "Event should be DefectReportedEvent");
        
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        String slackBody = event.slackBody();
        
        assertNotNull(slackBody, "Slack body should not be null");
        assertTrue(slackBody.contains(expectedUrl), "Slack body must contain the GitHub URL: " + slackBody);
        assertTrue(slackBody.contains("Defect Reported"), "Slack body should contain context");
    }

    @Test
    void shouldThrowExceptionIfGitHubUrlIsMissing() {
        // Arrange
        ValidationAggregate aggregate = new ValidationAggregate("agg-456");
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-455", 
            "Missing URL", 
            "No URL provided", 
            "", // Empty URL
            "MEDIUM"
        );

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });
        
        assertTrue(ex.getMessage().contains("GitHub URL is required"));
    }

    @Test
    void shouldRejectInvalidGitHubUrlFormat() {
        // Arrange
        ValidationAggregate aggregate = new ValidationAggregate("agg-789");
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-456", 
            "Bad URL", 
            "Not a github link", 
            "https://gitlab.com/something/else", 
            "LOW"
        );

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });
        
        assertTrue(ex.getMessage().contains("Invalid GitHub URL format"));
    }
}

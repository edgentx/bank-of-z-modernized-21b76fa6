package com.example.domain.validation;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for S-FB-1 / VW-454.
 * Validates that the Slack body contains the GitHub URL when provided via metadata.
 */
class ValidationAggregateVw454Test {

    @Test
    void shouldIncludeGitHubUrlInBodyWhenPresent() {
        // Arrange
        String aggregateId = "val-123";
        ValidationAggregate aggregate = new ValidationAggregate(aggregateId);
        
        String expectedUrl = "https://github.com/bank-of-z/issues/issues/454";
        Map<String, String> metadata = new HashMap<>();
        metadata.put("github_url", expectedUrl);

        ReportDefectCmd cmd = new ReportDefectCmd(
            "DEF-454", 
            "Slack body missing GitHub URL", 
            "LOW", 
            metadata
        );

        // Act
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Assert
        assertEquals(1, events.size());
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        
        assertNotNull(event.messageBody(), "Message body should not be null");
        
        // CRITICAL ASSERTION for VW-454
        // The body must contain a link to the issue
        assertTrue(
            event.messageBody().contains("<" + expectedUrl + "|Link>"), 
            "Slack body should include formatted GitHub URL"
        );
        
        // Also ensure the label is present
        assertTrue(
            event.messageBody().contains("GitHub Issue:"),
            "Slack body should contain 'GitHub Issue:' label"
        );
    }

    @Test
    void shouldHandleNullMetadataGracefully() {
        // Arrange
        ValidationAggregate aggregate = new ValidationAggregate("val-999");
        ReportDefectCmd cmd = new ReportDefectCmd(
            "DEF-999", 
            "Null Metadata Test", 
            "LOW", 
            null
        );

        // Act
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Assert
        assertEquals(1, events.size());
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        
        assertNotNull(event.messageBody());
        // Should not throw NPE
    }

    @Test
    void shouldHandleEmptyMetadataMap() {
        // Arrange
        ValidationAggregate aggregate = new ValidationAggregate("val-888");
        Map<String, String> metadata = new HashMap<>();
        ReportDefectCmd cmd = new ReportDefectCmd(
            "DEF-888", 
            "Empty Metadata Test", 
            "LOW", 
            metadata
        );

        // Act
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Assert
        assertEquals(1, events.size());
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        
        assertNotNull(event.messageBody());
        assertFalse(event.messageBody().contains("GitHub Issue:"));
    }
}

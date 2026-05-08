package com.example.domain.validation;

import com.example.domain.shared.Aggregate;
import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.DefectReportedEvent;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase: Defect Reported by user.
 * Testing S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 */
public class DefectReportedEventTest {

    /**
     * AC: The validation no longer exhibits the reported behavior
     * Context: Ensure that when a defect is reported, the resulting event
     * contains a valid GitHub URL.
     */
    @Test
    void whenReportDefectExecuted_eventContainsValidGitHubUrl() {
        // Arrange
        String defectId = "S-FB-1";
        String title = "Fix: Validating VW-454";
        String description = "Slack body includes GitHub issue";
        String severity = "LOW";
        String component = "validation";

        Aggregate aggregate = new ValidationAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId, 
            title, 
            description, 
            severity, 
            component
        );

        // Act
        List<DomainEvent> events = aggregate.execute(cmd);

        // Assert
        assertFalse(events.isEmpty(), "Expected at least one event to be produced");
        
        DomainEvent event = events.get(0);
        assertTrue(event instanceof DefectReportedEvent, "Expected DefectReportedEvent");

        DefectReportedEvent defectEvent = (DefectReportedEvent) event;
        
        // Validate that the GitHub URL is present and well-formed
        String githubUrl = defectEvent.githubIssueUrl();
        assertNotNull(githubUrl, "GitHub URL must not be null");
        assertFalse(githubUrl.isBlank(), "GitHub URL must not be blank");
        
        // Basic sanity check for URL structure
        assertTrue(githubUrl.startsWith("https://github.com/"), "URL must be a valid GitHub link");
        assertTrue(githubUrl.contains("/issues/"), "URL must point to an issue");
    }
}

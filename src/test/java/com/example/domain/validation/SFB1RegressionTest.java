package com.example.domain.validation;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.model.ValidationAggregate.DefectReportedEvent;
import com.example.domain.validation.model.ValidationAggregate.ReportDefectCommand;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for S-FB-1: Validating VW-454 — GitHub URL in Slack body (end-to-end).
 *
 * This test validates the domain logic responsible for generating the GitHub URL
 * that is eventually propagated to the Slack body.
 */
class SFB1RegressionTest {

    @Test
    void shouldContainGitHubUrlWhenDefectReported() {
        // Given
        String validationId = "vw-454-validation";
        String defectId = "S-FB-1";
        String expectedTitle = "Fix: Validating VW-454";
        // The expected URL format based on requirements
        String expectedUrl = "https://github.com/egdcrypto-bank-of-z/issues/S-FB-1";

        ValidationAggregate aggregate = new ValidationAggregate(validationId);
        ReportDefectCommand cmd = new ReportDefectCommand(
            validationId,
            defectId,
            expectedTitle,
            expectedUrl // Passing the URL that the workflow would have determined
        );

        // When
        List<DefectReportedEvent> events = aggregate.execute(cmd).stream()
            .filter(e -> e instanceof DefectReportedEvent)
            .map(e -> (DefectReportedEvent) e)
            .toList();

        // Then
        assertFalse(events.isEmpty(), "DefectReportedEvent should be raised");
        
        DefectReportedEvent event = events.get(0);
        assertEquals(expectedUrl, event.githubUrl(), "Event must contain the GitHub Issue URL");
        assertEquals("DefectReported", event.type());
        assertEquals(defectId, event.defectId());
        
        // Verify aggregate state reflects the reporting
        assertEquals(ValidationAggregate.Status.REPORTED, aggregate.getStatus());
        assertEquals(expectedUrl, aggregate.getGithubUrl(), "Aggregate state must store the GitHub URL");
    }

    @Test
    void shouldGenerateUrlIfNotProvidedInCommand() {
        // Given
        String validationId = "vw-454-auto";
        String defectId = "S-FB-1";
        String expectedUrl = "https://github.com/egdcrypto-bank-of-z/issues/S-FB-1";

        ValidationAggregate aggregate = new ValidationAggregate(validationId);
        ReportDefectCommand cmd = new ReportDefectCommand(
            validationId,
            defectId,
            "Title",
            null // Testing internal generation logic
        );

        // When
        List<DefectReportedEvent> events = aggregate.execute(cmd).stream()
            .filter(e -> e instanceof DefectReportedEvent)
            .map(e -> (DefectReportedEvent) e)
            .toList();

        // Then
        assertFalse(events.isEmpty());
        assertEquals(expectedUrl, events.get(0).githubUrl());
    }
}

package com.example.domain.validation;

import com.example.domain.validation.model.DefectReportedEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase: Verifying the domain event payload structure.
 * This ensures that when the defect is reported, the event carries
 * the necessary data to construct the GitHub URL.
 */
class DefectReportedEventTest {

    @Test
    void shouldContainGitHubIssueUrlInBody() {
        // Given
        String defectId = "VW-454";
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String expectedUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        
        // When
        DefectReportedEvent event = new DefectReportedEvent(
            defectId, 
            projectId, 
            "GitHub URL in Slack body", 
            Instant.now()
        );

        // Then
        String body = event.getSlackBody();
        assertNotNull(body, "Slack body should not be null");
        assertTrue(
            body.contains("GitHub issue:"), 
            "Slack body should contain the label 'GitHub issue:'"
        );
        assertTrue(
            body.contains(expectedUrl), 
            "Slack body should contain the full GitHub URL for VW-454"
        );
        assertTrue(
            body.contains("<" + expectedUrl + ">|"), 
            "Slack body should format the URL as a Slack link (<url>|text)"
        );
    }

    @Test
    void shouldHandleDifferentDefectIds() {
        // Given
        String defectId = "S-FB-1";
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String expectedUrl = "https://github.com/bank-of-z/vforce360/issues/S-FB-1";

        // When
        DefectReportedEvent event = new DefectReportedEvent(
            defectId,
            projectId,
            "Fix Validation",
            Instant.now()
        );

        // Then
        assertTrue(event.getSlackBody().contains(expectedUrl));
    }
}
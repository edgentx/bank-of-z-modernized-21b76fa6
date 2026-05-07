package com.example.e2e.regression;

import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for S-FB-1 (VW-454).
 * Validates that the GitHub URL is present in the Slack notification payload.
 * This test runs independently of Cucumber to ensure fast feedback in CI/CD pipelines.
 */
public class SFB1RegressionTest {

    @Test
    public void testDefectReport_GeneratesSlackBodyWithGitHubUrl() {
        // Arrange
        String defectId = "VW-454";
        String title = "Validating VW-454 — GitHub URL in Slack body";
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, title, "Description", null);
        VForce360Aggregate aggregate = new VForce360Aggregate(defectId);

        // Act
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Assert
        assertFalse(events.isEmpty(), "Should generate an event");
        
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertNotNull(event.githubUrl(), "GitHub URL must not be null");
        
        String body = event.slackBody();
        assertNotNull(body, "Slack body must not be null");
        
        // The core validation: URL must be in the body
        assertTrue(body.contains(event.githubUrl()), 
            "Regression Check Failed: Slack body must contain the GitHub URL. Story VW-454.");
        
        // Validation of format from Story Description
        assertTrue(body.contains("*Defect Detected:*"), "Should contain formatted title");
        assertTrue(body.contains("*Severity:* LOW"), "Should contain severity LOW");
        assertTrue(body.contains("*Link:*"), "Should contain Link label");
    }

    @Test
    public void testDefectReport_PreventsDuplicateReports() {
        // Arrange
        String defectId = "VW-455";
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, "Dup Test", "Desc", null);
        VForce360Aggregate aggregate = new VForce360Aggregate(defectId);
        
        // Act - First Execution
        aggregate.execute(cmd);
        
        // Assert - Second Execution
        assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        }, "Should not allow reporting the same defect twice");
    }
}

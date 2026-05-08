package com.example.e2e.regression;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.ReportDefectCmd;
import com.example.domain.vforce.model.DefectReportedEvent;
import com.example.domain.vforce.model.VForce360Aggregate;
import com.example.mocks.MockSlackNotifier;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for S-FB-1: VW-454 GitHub URL in Slack body.
 * 
 * Red Phase: This test expects the specific behavior described in the defect report.
 * It will fail against the current stub implementation of VForce360Aggregate.
 */
class SFB1DefectValidationTest {

    @Test
    void shouldIncludeGitHubIssueUrlInSlackBody() {
        // Arrange
        String defectId = "VW-454";
        String projectUid = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String expectedUrlFragment = "github.com/example/issues/" + defectId;
        
        VForce360Aggregate aggregate = new VForce360Aggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId, 
            "Validating VW-454 — GitHub URL in Slack body (end-to-end)", 
            "LOW", 
            projectUid
        );

        // Act
        List<DomainEvent> events = aggregate.execute(cmd);

        // Assert
        assertFalse(events.isEmpty(), "Expecting a DefectReportedEvent");
        
        DomainEvent event = events.get(0);
        assertTrue(event instanceof DefectReportedEvent, "Event must be DefectReportedEvent");
        
        DefectReportedEvent slackEvent = (DefectReportedEvent) event;
        String slackBody = slackEvent.slackBody();
        
        assertNotNull(slackBody, "Slack body should not be null");
        // Core assertion for VW-454
        assertTrue(
            slackBody.contains(expectedUrlFragment),
            "Slack body must contain GitHub issue URL for VW-454. Received: " + slackBody
        );
    }

    @Test
    void shouldPopulateSlackBodyWithValidStructure() {
        // Arrange
        VForce360Aggregate aggregate = new VForce360Aggregate("VW-123");
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-123", 
            "Test Defect", 
            "HIGH", 
            "proj-uid"
        );

        // Act
        List<DomainEvent> events = aggregate.execute(cmd);

        // Assert
        assertTrue(events.size() > 0);
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        
        // Basic structure validation
        assertNotNull(event.slackBody());
        assertFalse(event.slackBody().isBlank());
        assertNotNull(event.occurredAt());
    }
}

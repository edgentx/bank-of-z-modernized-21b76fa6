package com.example.domain.defect;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.mocks.MockSlackNotificationAdapter;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Test for VW-454.
 * 
 * Context:
 * Defect reported where the GitHub URL was missing or malformed in the Slack body 
 * when triggering `report_defect` via Temporal.
 * 
 * Expected:
 * Slack body includes GitHub issue: <url>
 */
@DisplayName("VW-454: Defect Reporting Validation")
class VW454DefectValidationTest {

    @Test
    @DisplayName("Given a valid ReportDefectCmd, When executed, Then DefectReportedEvent should contain the GitHub URL in the slack body")
    void testDefectReportContainsGitHubUrl() {
        // Arrange
        String defectId = "VW-454";
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String expectedUrl = "https://github.com/bank-of-z/issues/" + defectId;
        
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "Fix: Validating VW-454 — GitHub URL in Slack body",
            "Severity: LOW",
            projectId,
            null
        );

        DefectAggregate aggregate = new DefectAggregate(defectId);

        // Act
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Assert
        assertFalse(events.isEmpty(), "Should produce an event");
        
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        
        // Primary assertion for VW-454: Does the slackBody contain the URL?
        assertNotNull(event.slackBody(), "Slack body should not be null");
        assertTrue(
            event.slackBody().contains(expectedUrl),
            "Slack body MUST contain the GitHub URL. Expected to contain: " + expectedUrl + " but was: " + event.slackBody()
        );
    }

    @Test
    @DisplayName("Given a valid ReportDefectCmd, When executed, Then the GitHub URL field is populated")
    void testDefectReportGeneratesUrlField() {
        // Arrange
        String defectId = "S-FB-1";
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "Fix: Validating VW-454",
            "Description",
            "proj-id",
            null
        );

        DefectAggregate aggregate = new DefectAggregate(defectId);

        // Act
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Assert
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertNotNull(event.githubUrl(), "GitHub URL field should be populated");
    }
}
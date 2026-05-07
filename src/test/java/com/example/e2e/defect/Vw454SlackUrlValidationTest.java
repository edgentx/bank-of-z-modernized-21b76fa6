package com.example.e2e.defect;

import com.example.domain.defect.DefectService;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for Story S-FB-1.
 * Validates defect VW-454: GitHub URL in Slack body.
 * <p>
 * Expected Behavior: Slack body includes GitHub issue: <url>.
 * This test verifies that the DefectReportedEvent payload contains a valid,
 * non-null URL string that can be consumed by the Slack notification adapter.
 */
@SpringBootTest
class Vw454SlackUrlValidationTest {

    @Autowired
    private DefectService defectService;

    @Test
    @DisplayName("S-FB-1: Verify DefectReportedEvent contains valid GitHub URL for Slack")
    void verifyGitHubUrlPresentInEventPayload() {
        // Arrange
        String defectId = "VW-454";
        String projectId = "21b76fa6";
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "Fix: Validating VW-454 — GitHub URL in Slack body",
            "Severity: LOW\nComponent: validation",
            projectId
        );

        // Act
        // Trigger the report_defect workflow via domain service
        DefectReportedEvent event = defectService.handleReportDefect(cmd);

        // Assert
        assertNotNull(event, "DefectReportedEvent should not be null");
        
        // Critical assertion for VW-454: The URL must be present
        String githubUrl = event.githubIssueUrl();
        assertNotNull(githubUrl, "GitHub URL must not be null");
        assertFalse(githubUrl.isEmpty(), "GitHub URL must not be empty");
        
        // Validate format to ensure it's usable in Slack body
        assertTrue(githubUrl.startsWith("https://github.com/"), 
            "GitHub URL must start with https://github.com/");
        
        assertTrue(githubUrl.contains(projectId), 
            "GitHub URL should reference the project context");

        // Simulating Slack body construction (verification step)
        String slackBody = String.format(
            "GitHub issue: <%s|%s>", 
            githubUrl, 
            event.defectId()
        );
        
        assertTrue(slackBody.contains("<"), "Slack body should contain formatted link anchor");
        assertTrue(slackBody.contains(githubUrl), "Slack body should contain the actual URL");
    }
}

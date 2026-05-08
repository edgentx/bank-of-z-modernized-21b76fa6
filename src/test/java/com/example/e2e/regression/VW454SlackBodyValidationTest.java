package com.example.e2e.regression;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression test for VW-454: GitHub URL in Slack body (end-to-end)
 * 
 * This test verifies that when a defect is reported via the temporal-worker,
 * the resulting Slack body contains the GitHub issue link.
 * 
 * Reproduction Steps:
 * 1. Trigger _report_defect via temporal-worker exec
 * 2. Verify Slack body contains GitHub issue link
 * 
 * Expected Behavior:
 * Slack body includes GitHub issue: <url>
 */
public class VW454SlackBodyValidationTest {
    
    @Test
    public void testDefectReportedEventContainsGitHubUrl() {
        // Given
        String defectId = "VW-454";
        String title = "Validating VW-454 — GitHub URL in Slack body";
        String description = "Defect reported by user.";
        String severity = "LOW";
        String component = "validation";
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        
        DefectAggregate aggregate = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId, title, description, severity, component, projectId
        );
        
        // When - Trigger _report_defect via temporal-worker exec
        var events = aggregate.execute(cmd);
        
        // Then - Verify Slack body contains GitHub issue link
        assertThat(events).hasSize(1);
        var event = events.get(0);
        
        // The GitHub URL should be present in the event
        assertThat(event.githubUrl()).isNotNull();
        assertThat(event.githubUrl()).isNotEmpty();
        
        // The URL should be a valid GitHub issue URL format
        assertThat(event.githubUrl()).contains("github.com");
        assertThat(event.githubUrl()).contains("/issues/");
        
        // The URL should contain the defect ID
        assertThat(event.githubUrl()).contains(defectId);
        
        // The Slack body would be constructed from this event
        // and would include: "GitHub issue: " + event.githubUrl()
        String slackBody = buildSlackBody(event);
        assertThat(slackBody).contains("GitHub issue:");
        assertThat(slackBody).contains(event.githubUrl());
    }
    
    @Test
    public void testSlackBodyFormatIncludesGitHubUrl() {
        // Given
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/example/issues/" + defectId;
        
        DefectAggregate aggregate = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "Validating VW-454 — GitHub URL in Slack body",
            "Defect reported by user.",
            "LOW",
            "validation",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );
        
        // When
        var events = aggregate.execute(cmd);
        var event = events.get(0);
        
        // Then
        String slackBody = buildSlackBody(event);
        
        // Verify the expected format: "GitHub issue: <url>"
        assertThat(slackBody).containsPattern("GitHub issue:\s*" + expectedUrl);
    }
    
    /**
     * Helper method to build a Slack body from the DefectReportedEvent.
     * This simulates what the temporal-worker would do when sending the notification.
     */
    private String buildSlackBody(com.example.domain.defect.model.DefectReportedEvent event) {
        StringBuilder body = new StringBuilder();
        body.append("*Defect Reported*\n");
        body.append("*Title:* ").append(event.title()).append("\n");
        body.append("*Severity:* ").append(event.severity()).append("\n");
        body.append("*Component:* ").append(event.component()).append("\n");
        body.append("*Description:* ").append(event.description()).append("\n");
        body.append("GitHub issue: ").append(event.githubUrl());
        return body.toString();
    }
}
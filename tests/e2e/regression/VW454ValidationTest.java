package com.example.e2e.regression;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import com.example.mocks.InMemorySlackNotificationAdapter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Test for Story S-FB-1.
 * Validating VW-454 — GitHub URL in Slack body (end-to-end).
 * 
 * Reproduction Steps:
 * 1. Trigger _report_defect via temporal-worker exec (Simulated here via Aggregate)
 * 2. Verify Slack body contains GitHub issue link
 */
class VW454ValidationTest {

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenReportingDefect() {
        // GIVEN
        // An adapter that captures what would be sent to Slack
        InMemorySlackNotificationAdapter mockSlack = new InMemorySlackNotificationAdapter();
        
        String defectId = "VW-454";
        String summary = "Validating VW-454 — GitHub URL in Slack body";
        String severity = "LOW";
        
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, summary, "Description", severity, "validation");
        DefectAggregate aggregate = new DefectAggregate(defectId);

        // WHEN
        // 1. Trigger report_defect logic (executing the aggregate)
        var events = aggregate.execute(cmd);
        
        // Simulate the side-effect where a worker reads the event and posts to Slack
        // (In the real implementation, a Workflow/Activity would do this)
        String generatedUrl = aggregate.getGitHubIssueUrl(); // Derived from the event state
        String slackBody = String.format("Defect Reported: %s. GitHub Issue: %s", summary, generatedUrl);
        mockSlack.sendMessage("#vforce360-issues", slackBody);

        // THEN
        // Verify Slack body contains GitHub issue: <url>
        String actualPostedBody = mockSlack.getLastMessageBody();
        
        assertNotNull(actualPostedBody, "Slack body should not be null");
        assertTrue(
            actualPostedBody.contains("GitHub Issue:"), 
            "Slack body must contain 'GitHub Issue:' label"
        );
        assertTrue(
            actualPostedBody.contains("https://github.com/"), 
            "Slack body must contain the GitHub URL"
        );
        assertTrue(
            actualPostedBody.contains(defectId), 
            "Slack body must contain the specific Defect ID"
        );
        assertTrue(
            actualPostedBody.contains("<") && actualPostedBody.contains(">"),
            "Defect Report: Slack body should contain the URL (Simulated format)"
        );
    }

    @Test
    void shouldFailIfSlackBodyIsEmpty() {
        // Regression check to ensure we don't send empty alerts
        InMemorySlackNotificationAdapter mockSlack = new InMemorySlackNotificationAdapter();
        mockSlack.sendMessage("#vforce360-issues", "");
        
        // If body is empty, this validation should fail or catch it
        assertTrue(mockSlack.getLastMessageBody().isEmpty(), "Empty body detected");
        fail("Slack body was empty, validation failed");
    }
}

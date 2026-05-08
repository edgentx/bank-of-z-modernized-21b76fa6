package com.example.e2e.regression;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for S-FB-1.
 * AC: Regression test added to e2e/regression/ covering this scenario
 * 
 * Scenario:
 * 1. Trigger _report_defect via temporal-worker exec (simulated here by Aggregate execution)
 * 2. Verify Slack body contains GitHub issue link
 */
public class SFB1RegressionTest {

    private MockSlackNotificationPort mockSlack;

    @BeforeEach
    void setUp() {
        mockSlack = new MockSlackNotificationPort();
    }

    @Test
    void validateVW454_GitHubUrlInSlackBody() {
        // Arrange
        // Simulating the Temporal worker triggering the report
        ValidationAggregate aggregate = new ValidationAggregate("S-FB-1");
        ReportDefectCmd cmd = new ReportDefectCmd(
            "S-FB-1",
            "Fix: Validating VW-454",
            "Slack body should include GitHub issue",
            "LOW",
            "validation"
        );

        // Act
        // 1. Trigger the defect reporting workflow logic
        var events = aggregate.execute(cmd);
        
        // Extract the URL from the domain event (Simulating the projection/service layer preparing the Slack message)
        assertTrue(events.size() > 0, "Event should be raised");
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        String githubUrl = event.githubIssueUrl();
        
        String slackBody = String.format("Defect Reported: %s\nGitHub Issue: %s", event.title(), githubUrl);
        
        // 2. Send to Slack (using mock)
        mockSlack.sendNotification("#vforce360-issues", slackBody);

        // Assert
        // Verify the Slack body contains the GitHub issue link
        assertEquals(1, mockSlack.sentBodies.size(), "One notification should be sent");
        
        String capturedBody = mockSlack.sentBodies.get(0);
        assertTrue(
            capturedBody.contains("https://github.com/"), 
            "Slack body must contain a GitHub URL"
        );
        assertTrue(
            capturedBody.contains("/issues/"), 
            "Slack body must contain an issue link"
        );
    }
}

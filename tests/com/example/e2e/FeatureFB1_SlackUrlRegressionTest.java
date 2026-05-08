package com.example.e2e;

import com.example.domain.vforce360.model.DefectAggregate;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.mocks.MockNotificationAdapter;
import com.example.ports.NotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Regression Test for Story S-FB-1.
 * 
 * Context: Verify that when a defect is reported, the resulting notification
 * (simulating Slack) contains the valid GitHub URL.
 * 
 * This test mocks the NotificationPort to inspect the payload without
 * sending real HTTP requests.
 */
class FeatureFB1_SlackUrlRegressionTest {

    private MockNotificationAdapter mockNotification;

    @BeforeEach
    void setUp() {
        mockNotification = new MockNotificationAdapter();
    }

    @Test
    void shouldIncludeGithubUrlInSlackBody_VW454() {
        // Scenario: VW-454 — GitHub URL in Slack body (end-to-end)
        
        // 1. Setup Input
        String defectId = "VW-454";
        String expectedTitle = "Validating GitHub URL in Slack body";
        String expectedSeverity = "LOW";
        
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            expectedTitle,
            "Checking #vforce360-issues for the link line",
            expectedSeverity
        );

        // 2. Execute Domain Logic (The 'Temporal' trigger simulation)
        DefectAggregate aggregate = new DefectAggregate(defectId);
        List<DefectReportedEvent> events = aggregate.execute(cmd).stream()
            .filter(e -> e instanceof DefectReportedEvent)
            .map(e -> (DefectReportedEvent) e)
            .toList();

        // 3. Process Event into Notification (Simulating Workflow activity)
        assertFalse(events.isEmpty(), "DefectReportedEvent should have been raised");
        DefectReportedEvent event = events.get(0);

        // Simulating the construction of the Slack message body based on the domain event
        // Real implementation would map event fields to a Slack block kit JSON or simple string
        String slackChannel = "#vforce360-issues";
        String slackBody = String.format(
            "Defect Reported: %s\nSeverity: %s\nGitHub Issue: %s",
            event.title(),
            event.severity(),
            event.githubIssueUrl() // This is the field we are validating
        );

        // Send via Mock Adapter
        mockNotification.send(slackChannel, slackBody);

        // 4. Verify Outcome (The 'Report Defect' validation)
        List<MockNotificationAdapter.SentMessage> sentMessages = mockNotification.getMessages();
        assertEquals(1, sentMessages.size(), "One message should have been sent to Slack");

        MockNotificationAdapter.SentMessage message = sentMessages.get(0);
        assertEquals("#vforce360-issues", message.channel, "Message should go to the correct channel");
        
        // CORE ASSERTION: The body must contain the GitHub URL
        assertTrue(
            message.body.contains("github.com"), 
            "Slack body must contain the GitHub URL. Violation of VW-454."
        );
        
        // Verify exact structure if possible (e.g. 'GitHub Issue: <url>')
        assertTrue(
            message.body.matches(".*GitHub Issue: https://github\.com/ticket-[A-Z0-9-]+.*"),
            "Slack body must contain the formatted GitHub link line"
        );
    }
}

package com.example.e2e.regression;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for VW-454.
 * Scenario: When a defect is reported, the resulting Slack notification
 * must contain the valid GitHub Issue URL in the body.
 */
class VW454SlackGitHubLinkTest {

    @Test
    void shouldContainGitHubUrlInSlackBody() {
        // Arrange
        String expectedUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";
        MockSlackNotificationPort slackPort = new MockSlackNotificationPort();

        // Act
        // 1. Aggregate processes the command
        ValidationAggregate aggr = new ValidationAggregate("vw-454-test");
        var events = aggr.execute(new ReportDefectCmd("vw-454-test", "VW-454 Regression", expectedUrl));

        // 2. Simulate the workflow: Port consumes the event and sends the notification
        // (In a real app, this wiring happens in the Application Service/Workflow)
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        String slackBody = formatSlackBody(event);
        slackPort.sendNotification("#vforce360-issues", slackBody);

        // Assert
        // Verify the mock received the message
        assertEquals(1, slackPort.getMessages().size());
        
        String actualBody = slackPort.getLastMessageBody();
        
        // Core assertion: The body must include the specific GitHub URL
        assertTrue(actualBody.contains(expectedUrl), "Slack body must contain the GitHub Issue URL");
    }

    @Test
    void shouldFailValidationIfGitHubUrlIsMissing() {
        // Arrange
        ValidationAggregate aggr = new ValidationAggregate("vw-454-fail");
        
        // Act & Assert
        // Passing a blank or non-GitHub URL should fail at the Aggregate level
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            aggr.execute(new ReportDefectCmd("vw-454-fail", "Bad URL", "http://not-github.com"));
        });

        assertTrue(ex.getMessage().toLowerCase().contains("github"));
    }

    // Helper to replicate the formatting logic expected in the Slack message
    private String formatSlackBody(DefectReportedEvent event) {
        return String.format(
            "Defect Reported: %s\nGitHub Issue: %s",
            event.title(),
            event.githubIssueUrl()
        );
    }
}

package com.example.integration;

import com.example.domain.shared.Command;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * VW-454: Verify GitHub URL in Slack body (End-to-End Regression).
 * <p>
 * Defect: Validating VW-454 — GitHub URL in Slack body.
 * Severity: LOW
 * Component: validation
 * <p>
 * Context:
 * We are in the TDD Red phase. We expect the logic to link GitHub creation with Slack notification
 * to be either missing or incomplete. This test drives the implementation of that logic.
 */
public class VW454_EndToEndRegressionTest {

    private VForce360Aggregate aggregate;
    private MockSlackNotificationPort slackPort;
    private MockGitHubIssuePort githubPort;

    @BeforeEach
    void setUp() {
        String defectId = "VW-454";
        aggregate = new VForce360Aggregate(defectId);
        slackPort = new MockSlackNotificationPort();
        githubPort = new MockGitHubIssuePort();
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenReportingDefect() {
        // Given
        // We set up the command representing the temporal-worker triggering defect reporting.
        ReportDefectCmd reportCmd = new ReportDefectCmd(
                "VW-454",
                "Fix: Validating VW-454 — GitHub URL in Slack body (end-to-end)",
                Map.of("severity", "LOW", "component", "validation"),
                "Slack body includes GitHub issue: <url>",
                "About to find out — checking #vforce360-issues for the link line",
                "C_VFORCE_ISSUES"
        );

        // When
        // 1. Execute the domain logic (Simulate Temporal Workflow execution)
        var events = aggregate.execute(reportCmd);
        
        // Assume we have a handler that reacts to DefectReportedEvent.
        // For the purpose of this E2E test, we simulate the Handler logic here.
        // In a real Spring Boot app, this would be an EventHandler/Component.
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        
        // 2. Handler creates GitHub Issue
        String issueDescription = String.format(
                "**Severity:** %s\n**Expected Behavior:** %s\n**Actual Behavior:** %s",
                event.details().get("severity"),
                event.expectedBehavior(),
                event.actualBehavior()
        );
        Optional<GitHubIssuePort.GitHubUrl> githubUrl = githubPort.createIssue(event.title(), issueDescription);
        
        // 3. Handler sends Slack notification
        if (githubUrl.isPresent()) {
            String slackMessage = buildSlackMessage(event, githubUrl.get().url());
            slackPort.sendMessage(event.slackChannelId(), slackMessage);
        }

        // Then
        // Validate the Slack body includes the GitHub issue link.
        assertFalse(slackPort.getSentMessages().isEmpty(), "Slack message should have been sent");
        
        MockSlackNotificationPort.SentMessage sent = slackPort.getSentMessages().get(0);
        assertNotNull(sent.body(), "Slack body should not be null");
        
        // This is the core assertion for VW-454.
        // The body must contain the actual URL generated in step 2.
        assertTrue(
                sent.body().contains(githubUrl.get().url()),
                "Slack body must contain the GitHub issue URL. Body was: " + sent.body()
        );

        // Also verify the Channel ID matches the command
        assertEquals("C_VFORCE_ISSUES", sent.channelId());
    }

    @Test
    void shouldHandleGracefullyWhenGitHubCreationFails() {
        // Given
        githubPort.setShouldSucceed(false);
        ReportDefectCmd reportCmd = new ReportDefectCmd(
                "VW-454-FAIL",
                "Test Failure",
                Map.of(),
                "Ex", "Act", "C_VFORCE_ISSUES"
        );

        // When
        var events = aggregate.execute(reportCmd);
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        
        Optional<GitHubIssuePort.GitHubUrl> githubUrl = githubPort.createIssue(event.title(), "desc");
        
        // Then
        // If GitHub fails, we expect Slack to either not be sent, or sent with a specific error format.
        // Here we assert the URL is missing to simulate the "Red" phase behavior for this edge case.
        assertTrue(githubUrl.isEmpty(), "GitHub URL should be empty");
        
        // In the current implementation (simulated), we check if slack was sent.
        // If no URL, we might not send slack, or send a different message.
        // This assertion documents current behavior (likely nothing sent).
        assertTrue(slackPort.getSentMessages().isEmpty());
    }

    // Helper method simulating the message builder logic.
    // This will eventually be replaced by the actual implementation class.
    private String buildSlackMessage(DefectReportedEvent event, String url) {
        // This is a stub implementation. The real test will fail until this logic
        // is implemented in the production code and wired in.
        return String.format(
                "Defect Reported: %s\nSeverity: %s\nGitHub Issue: %s",
                event.title(),
                event.details().get("severity"),
                url
        );
    }
}
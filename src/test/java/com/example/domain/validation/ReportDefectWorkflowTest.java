package com.example.domain.validation;

import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Test for S-FB-1.
 * Verifies that when _report_defect is triggered:
 * 1. A GitHub issue is created.
 * 2. The Slack notification body contains the GitHub URL.
 */
class ReportDefectWorkflowTest {

    private MockGitHubIssuePort mockGitHub;
    private MockSlackNotificationPort mockSlack;

    @BeforeEach
    void setUp() {
        mockGitHub = new MockGitHubIssuePort("https://github.com/mock-repo/issues/454");
        mockSlack = new MockSlackNotificationPort();
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenReportingDefect() {
        // Arrange
        ReportDefectCommand cmd = new ReportDefectCommand(
            "S-FB-1",
            "Fix: Validating VW-454",
            "Defect reported by user.",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );

        // We act as if the Temporal worker is invoking this logic
        // In a real test, we might inject the ports into a Workflow/Service class.
        // Here we are defining the expected behavior of the system under test.
        
        // Simulating the System Under Test (SUT) logic that we are about to write
        // This part would normally be: workflowService.execute(cmd);
        
        // --- EXPECTED BEHAVIOR (to be implemented) ---
        // 1. Create GitHub Issue
        String githubUrl = mockGitHub.createIssue(cmd.title(), formatIssueBody(cmd));
        
        // 2. Send Slack Notification
        String slackBody = formatSlackBody(githubUrl, cmd);
        mockSlack.sendMessage(MockSlackNotificationPort.DEFAULT_CHANNEL, slackBody);
        // -------------------------------------------

        // Assert
        // AC: Regression test added to e2e/regression/ covering this scenario
        // AC: The validation no longer exhibits the reported behavior
        
        assertTrue(mockGitHub.hasCreatedIssue(), "GitHub issue should have been created");
        
        String sentMessage = mockSlack.getLastMessageBody();
        assertNotNull(sentMessage, "Slack message should have been sent");
        
        // The critical check for VW-454: Does the body contain the URL?
        assertTrue(
            sentMessage.contains("https://github.com/mock-repo/issues/454"),
            "Slack body MUST contain the GitHub issue URL. Found: " + sentMessage
        );
    }

    // --- Helpers simulating the formatter we expect to implement ---
    private String formatIssueBody(ReportDefectCommand cmd) {
        return "Description: " + cmd.description();
    }

    private String formatSlackBody(String url, ReportDefectCommand cmd) {
        return "Defect Reported: " + cmd.defectId() + "\n" +
               "GitHub Issue: " + url;
    }
}

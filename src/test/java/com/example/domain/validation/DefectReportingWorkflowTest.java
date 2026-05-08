package com.example.domain.validation;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Test.
 * Story: S-FB-1
 * Description: Verify that when a defect is reported via Temporal, the resulting Slack message
 * contains the valid GitHub URL.
 */
public class DefectReportingWorkflowTest {

    private MockGitHubPort mockGitHub;
    private MockSlackNotificationPort mockSlack;

    @BeforeEach
    public void setUp() {
        mockGitHub = new MockGitHubPort();
        mockSlack = new MockSlackNotificationPort();
    }

    @Test
    public void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Given
        // We simulate the "report_defect" command with a summary and details.
        String defectTitle = "VW-454: Validation error in transfer saga";
        String defectDetails = "Severity: LOW\nComponent: validation";
        String targetChannel = "#vforce360-issues";

        // The Workflow/Service we are testing (implementation doesn't exist yet).
        DefectReportingWorkflow workflow = new DefectReportingWorkflow(mockGitHub, mockSlack);

        // When
        workflow.executeReportDefect(defectTitle, defectDetails, targetChannel);

        // Then
        // 1. Verify GitHub was called.
        // 2. Verify Slack was called.
        assertFalse(mockSlack.messages.isEmpty(), "Slack should have received a message");

        MockSlackNotificationPort.SlackMessage msg = mockSlack.messages.get(0);
        assertEquals(targetChannel, msg.channel(), "Message should go to the correct channel");

        // 3. Critical Validation: The body must contain the URL returned by GitHub.
        //    Since MockGitHub returns "https://github.com/example/repo/issues/1", we expect that in the body.
        assertTrue(msg.body().contains("https://github.com/example/repo/issues/1"),
            "Slack body must contain the GitHub issue URL. Actual body: " + msg.body());
    }

    @Test
    public void testReportDefect_ShouldHandleMultipleLinksCorrectly() {
        // Given
        mockGitHub.setIssueCounter(10); // Simulate issue #10
        DefectReportingWorkflow workflow = new DefectReportingWorkflow(mockGitHub, mockSlack);

        // When
        workflow.executeReportDefect("S-17", "Details", "#general");

        // Then
        MockSlackNotificationPort.SlackMessage msg = mockSlack.messages.get(0);
        assertTrue(msg.body().contains("/issues/10"), "Should contain the specific generated URL");
    }
}

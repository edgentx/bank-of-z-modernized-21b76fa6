package com.example.vforce;

import com.example.ports.SlackNotificationPort;
import com.example.ports.GithubIssuePort;
import com.example.mocks.MockSlackNotificationAdapter;
import com.example.mocks.MockGithubIssueAdapter;
import com.example.domain.shared.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Test.
 * Verifies that the ReportDefectWorkflow correctly bridges GitHub creation
 * and Slack notifications to satisfy S-FB-1.
 */
public class DefectReportWorkflowTest {

    private MockGithubIssueAdapter mockGithub;
    private MockSlackNotificationAdapter mockSlack;
    private ReportDefectWorkflow workflow;

    @BeforeEach
    void setUp() {
        mockGithub = new MockGithubIssueAdapter();
        mockSlack = new MockSlackNotificationAdapter();
        workflow = new ReportDefectWorkflow(mockGithub, mockSlack);
    }

    @Test
    @DisplayName("S-FB-1: Should post Slack message containing GitHub URL when defect is reported")
    void testSlackBodyContainsGithubUrl() {
        // Arrange: Setup the defect command
        ReportDefectWorkflow.ReportDefectCmd cmd = new ReportDefectWorkflow.ReportDefectCmd(
            "VW-454",
            "Validating GitHub URL in Slack body",
            "Severity: LOW"
        );

        // Act: Execute the workflow
        workflow.execute(cmd);

        // Assert:
        // 1. Verify GitHub was called (sanity check)
        assertTrue(mockGithub.wasCreateCalled(), "GitHub issue creation should be triggered");
        
        // 2. Verify Slack was called
        assertTrue(mockSlack.wasNotifyCalled(), "Slack notification should be triggered");

        // 3. CRITICAL ASSERTION for S-FB-1
        // The Slack body must contain the URL returned by the GitHub port.
        String slackBody = mockSlack.getCapturedBody();
        String expectedUrl = mockGithub.getReturnedUrl();

        assertNotNull(slackBody, "Slack body should not be null");
        assertTrue(
            slackBody.contains(expectedUrl),
            "Slack body must contain the GitHub issue URL. Expected to contain: " + expectedUrl + " but was: " + slackBody
        );
    }

    @Test
    @DisplayName("Should handle null URL from GitHub gracefully without throwing NPE")
    void testHandlesNullUrlGracefully() {
        // Edge case: GitHub returns null (e.g. creation failed)
        mockGithub.setReturnNullUrl(true);

        ReportDefectWorkflow.ReportDefectCmd cmd = new ReportDefectWorkflow.ReportDefectCmd(
            "VW-999", "Null URL Test", "HIGH"
        );

        // Act: Execute the workflow
        workflow.execute(cmd);

        // Assert: Workflow should still attempt to notify slack, possibly with a fallback message
        assertTrue(mockSlack.wasNotifyCalled());
        String body = mockSlack.getCapturedBody();
        // We expect a graceful failure or placeholder, not an NPE crash
        assertNotNull(body);
    }
}

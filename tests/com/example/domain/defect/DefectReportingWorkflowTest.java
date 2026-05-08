package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import mocks.MockGitHubPort;
import mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Test for S-FB-1.
 * Validates that the Defect Reporting Workflow:
 * 1. Creates a GitHub Issue.
 * 2. Includes the GitHub Issue URL in the Slack notification body.
 * 
 * This test assumes the existence of a Workflow/Orchestrator class 
 * (e.g., {@code DefectReportingWorkflow}) that coordinates these ports.
 * 
 * <b>Note:</b> We are not using Temporal TestEnvironment here to keep the unit test
 * fast and focused on the business logic flow. The Temporal binding is an
 * implementation detail of the worker.
 */
class DefectReportingWorkflowTest {

    private MockSlackNotificationPort slackPort;
    private MockGitHubPort gitHubPort;

    // This is the class under test. It likely doesn't exist yet (Red Phase).
    // In the actual implementation, this might be a Temporal ActivityImpl or a Service.
    private DefectReportingWorkflow workflow; // Hypothetical class

    @BeforeEach
    void setUp() {
        slackPort = new MockSlackNotificationPort();
        gitHubPort = new MockGitHubPort();
        
        // Inject mocks. The implementation class needs to support this.
        workflow = new DefectReportingWorkflow(slackPort, gitHubPort);
    }

    @Test
    void whenReportingDefect_shouldCreateGitHubIssueAndPostToSlackWithLink() {
        // Arrange
        String expectedIssueUrl = "https://github.com/example/bank-of-z/issues/454";
        gitHubPort.setNextIssueUrl(expectedIssueUrl);

        ReportDefectCmd cmd = new ReportDefectCmd(
            "DEF-454",
            "VW-454: GitHub URL in Slack body",
            "Verifying the defect link works",
            "LOW"
        );

        // Act
        // This method orchestrates the defect reporting process.
        // It calls GitHubPort.createIssue, then uses the result to call SlackPort.postMessage.
        workflow.execute(cmd);

        // Assert
        // 1. Verify Slack was called
        assertEquals(1, slackPort.getMessages().size(), "Slack should be called once");

        // 2. Verify the URL is in the body
        MockSlackNotificationPort.Message slackMsg = slackPort.getLastMessage();
        assertTrue(
            slackMsg.body.contains(expectedIssueUrl),
            "Slack body must contain the GitHub Issue URL. Expected: " + expectedIssueUrl + " but got: " + slackMsg.body
        );
        
        // 3. Verify correct channel
        assertEquals("#vforce360-issues", slackMsg.channel);
    }

    @Test
    void whenGitHubFails_shouldStillNotifySlackWithErrorMessage() {
        // Arrange
        gitHubPort.setShouldFail(true); // Simulate GitHub API failure
        ReportDefectCmd cmd = new ReportDefectCmd(
            "DEF-999", "Critical Failure", "System down", "HIGH"
        );

        // Act
        workflow.execute(cmd);

        // Assert
        MockSlackNotificationPort.Message slackMsg = slackPort.getLastMessage();
        assertTrue(slackMsg.body.contains("Failed to create GitHub issue"));
    }
}

package com.example.e2e.regression;

import com.example.domain.shared.Command;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * End-to-End (E2E) Regression Test for Defect VW-454.
 * 
 * Context: Verifies that when a defect report is triggered via the temporal workflow,
 * the Slack notification body includes the link to the created GitHub issue.
 * 
 * This test uses Mock Adapters to simulate external dependencies (GitHub, Slack).
 */
public class VW454RegressionTest {

    private MockGitHubPort mockGitHub;
    private MockSlackNotificationPort mockSlack;
    private ReportDefectWorkflowHandler workflowHandler; // System Under Test (SUT)

    @BeforeEach
    void setUp() {
        mockGitHub = new MockGitHubPort();
        mockSlack = new MockSlackNotificationPort();
        // In a real Spring Boot test, we would inject these mocks. 
        // Here we manually wire them for the unit-test style execution.
        workflowHandler = new ReportDefectWorkflowHandler(mockGitHub, mockSlack);
    }

    @Test
    void shouldVerifySlackBodyContainsGitHubIssueUrl() {
        // Arrange (Reproduction Step 1: Trigger _report_defect)
        String defectId = "S-FB-1";
        String expectedStoryId = "S-FB-1";
        String title = "Fix: Validating VW-454 — GitHub URL in Slack body";
        String description = "Defect reported by user. Severity: LOW.";
        
        // We use the Command object that would be passed by Temporal
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            expectedStoryId,
            title,
            "LOW",
            "validation"
        );

        // Act (Simulate Temporal Worker Execution)
        // The handler should: 1. Process Command, 2. Call GitHub, 3. Call Slack with URL.
        workflowHandler.handleReportDefect(cmd);

        // Assert (Reproduction Step 2: Verify Slack body contains link)
        // 1. Verify GitHub was called
        boolean githubCalled = !mockGitHub.getCreatedIssues().isEmpty();
        String githubUrl = mockGitHub.getCreatedIssues().get(0);
        
        assertTrue(githubCalled, "GitHub port should have been called to create an issue");
        assertEquals("https://github.com/mocked-repo/issues/1", githubUrl);

        // 2. Verify Slack was called
        boolean slackCalled = !mockSlack.getPostedMessages().isEmpty();
        assertTrue(slackCalled, "Slack port should have been called");

        // 3. Verify the Content (The Core Fix for VW-454)
        // Expected Behavior: Slack body includes GitHub issue: <url>
        boolean containsUrl = mockSlack.lastMessageContains(githubUrl);
        
        assertTrue(containsUrl, 
            "Slack notification body MUST contain the GitHub Issue URL. " +
            "Defect VW-454 indicates the link was missing. Found URL: " + githubUrl);
    }
}

/**
 * Placeholder class for the component responsible for the workflow logic.
 * In a real implementation, this would be a Temporal Activity or Workflow implementation.
 * For this test suite (Red Phase), this class does not exist yet or is empty.
 */
class ReportDefectWorkflowHandler {
    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackPort;

    public ReportDefectWorkflowHandler(GitHubPort gitHubPort, SlackNotificationPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    public void handleReportDefect(Command cmd) {
        // Implementation missing to ensure test fails (Red Phase).
        // Logic required:
        // 1. Extract details from cmd.
        // 2. String issueUrl = gitHubPort.createIssue(...);
        // 3. String slackBody = "Issue created: " + issueUrl;
        // 4. slackPort.postDefectNotification(cmd, slackBody);
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

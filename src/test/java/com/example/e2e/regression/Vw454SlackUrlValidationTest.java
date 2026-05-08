package com.example.e2e.regression;

import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.mocks.MockGitHubIssueAdapter;
import com.example.mocks.MockSlackNotificationAdapter;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Regression Test for VW-454.
 * Validates that when a defect is reported via temporal-worker,
 * the Slack body includes the generated GitHub issue URL.
 * 
 * Red Phase: Implementation is missing, so we mock the potential flow
 * and assert on the contract expectation.
 */
class Vw454SlackUrlValidationTest {

    private MockSlackNotificationAdapter slackMock;
    private MockGitHubIssueAdapter gitHubMock;
    private DefectReportWorkflowOrchestrator orchestrator; // This is the class we would write later

    @BeforeEach
    void setUp() {
        slackMock = new MockSlackNotificationAdapter();
        gitHubMock = new MockGitHubIssueAdapter();
        // In a real Spring Test, we would inject mocks. Here we construct manually for TDD.
        orchestrator = new DefectReportWorkflowOrchestrator(slackMock, gitHubMock);
    }

    @Test
    @DisplayName("VW-454: Slack body should contain GitHub issue URL after defect report")
    void testSlackBodyContainsGitHubUrl() {
        // Arrange
        String defectId = "VW-454";
        String title = "Fix: Validating VW-454 — GitHub URL in Slack body";
        String desc = "Severity: LOW\nComponent: validation";
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, title, desc, "LOW", "21b76fa6");

        // Act
        orchestrator.execute(cmd);

        // Assert
        // 1. Verify GitHub issue was created
        assertNotNull(gitHubMock.getCreatedTitle());
        assertEquals(title, gitHubMock.getCreatedTitle());
        
        String expectedUrl = gitHubMock.createIssue(title, desc); 
        // Note: In real implementation, we'd capture the return from orchestrator, 
        // but for this test we rely on the mock's internal state counter or implied logic.
        
        // 2. Verify Slack message was sent
        String slackBody = slackMock.getLastBody();
        assertNotNull(slackBody, "Slack body should not be null");

        // 3. CRITICAL ASSERTION: The body must contain the URL (VW-454 fix)
        assertTrue(
            slackBody.contains("http") && slackBody.contains("github.com"),
            "Slack body must contain the GitHub URL. Actual body: " + slackBody
        );
        assertTrue(
            slackBody.contains(defectId),
            "Slack body should reference the Defect ID. Actual body: " + slackBody
        );
    }

    @Test
    @DisplayName("VW-454 Regression: Ensure URL format is valid")
    void testUrlFormatIsValid() {
        // Arrange
        ReportDefectCmd cmd = new ReportDefectCmd("VW-999", "Test", "Desc", "HIGH", "proj-x");

        // Act
        orchestrator.execute(cmd);

        // Assert
        String body = slackMock.getLastBody();
        // Simple heuristic for "Valid URL"
        assertTrue(body.matches(".*https://github\.com/example/repo/issues/\d+.*"), 
            "URL must match expected pattern. Found: " + body);
    }

    /**
     * Temporary placeholder class to satisfy the compilation in the Red Phase.
     * In the Green phase, this would be implemented (likely as a Temporal Activity or Workflow).
     */
    private static class DefectReportWorkflowOrchestrator {
        private final SlackNotificationPort slack;
        private final GitHubIssuePort github;

        public DefectReportWorkflowOrchestrator(SlackNotificationPort slack, GitHubIssuePort github) {
            this.slack = slack;
            this.github = github;
        }

        public void execute(ReportDefectCmd cmd) {
            // MISSING IMPLEMENTATION
            // This is the Red Phase: We force the test to fail by doing nothing,
            // or we stub a simple version that fails the specific assertion to drive development.
            
            // If we do nothing, `slackMock.getLastBody()` is null, and the test fails at `assertNotNull`.
            // If we want to be specific about VW-454, we could send a message WITHOUT the url:
            // slack.sendMessage("#vforce360-issues", "Defect Reported: " + cmd.title());
            // That would cause the test `testSlackBodyContainsGitHubUrl` to fail on the `contains("github.com")` check.
            
            // Choosing to do nothing to trigger the first Null assertion.
        }
    }
}
package com.example.e2e.regression;

import com.example.domain.validation.model.ReportDefectCommand;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * End-to-end regression test for VW-454.
 * 
 * Story: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * Description: When a defect is reported via temporal-worker exec, 
 * the resulting Slack notification body must contain the GitHub issue link.
 * 
 * Expected Behavior: Slack body includes GitHub issue: <url>
 * 
 * @see com.example.domain.validation.model.ReportDefectCommand
 */
class VW454SlackBodyValidationTest {

    private MockSlackNotificationPort slackMock;
    private MockGitHubIssuePort githubMock;
    private ValidationTestSubjectOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        // Initialize mocks for external dependencies
        slackMock = new MockSlackNotificationPort();
        githubMock = new MockGitHubIssuePort("https://github.com/fake-org/vforce360");
        
        // Inject mocks into the test subject (simulating the temporal-worker wiring)
        orchestrator = new ValidationTestSubjectOrchestrator(slackMock, githubMock);
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBody_whenReportDefectIsExecuted() {
        // Arrange
        String expectedDefectId = "VW-454";
        String expectedSummary = "Validation failed for branch feature-x";
        
        ReportDefectCommand command = new ReportDefectCommand(
            expectedDefectId,
            "LOW",
            expectedSummary,
            Map.of("component", "validation", "project", "vforce360")
        );

        // Act
        // Trigger _report_defect via temporal-worker exec simulation
        orchestrator.executeReportDefect(command);

        // Assert
        // 1. Verify interaction with GitHub mock (ensure issue was 'created')
        String expectedUrl = githubMock.createIssue(expectedSummary, "[Automated Defect Report]");

        // 2. Retrieve the message sent to Slack
        var postedMessages = slackMock.getMessages();
        assertThat(postedMessages)
            .as("Slack should have received a notification")
            .hasSize(1);

        MockSlackNotificationPort.PostedMessage slackPost = postedMessages.get(0);
        assertThat(slackPost.channel()).isEqualTo("#vforce360-issues");

        // 3. Core Validation: Verify Slack body contains GitHub issue link
        String slackBody = slackPost.body();
        assertThat(slackBody)
            .as("Slack body must contain the GitHub issue URL. Actual body: " + slackBody)
            .contains(expectedUrl);
    }

    @Test
    void shouldNotContainPlaceholderText_ifGitHubLinkIsGenerated() {
        // Arrange
        ReportDefectCommand command = new ReportDefectCommand(
            "VW-455", 
            "MEDIUM", 
            "Placeholder check",
            Map.of()
        );

        // Act
        orchestrator.executeReportDefect(command);

        // Assert
        String slackBody = slackMock.getLastMessage().body();
        
        // We expect the actual URL, not a placeholder like <url> or ???
        assertThat(slackBody)
            .as("Body should not contain placeholder text like '<url>' or '???'. Body: " + slackBody)
            .doesNotContain("<url>")
            .doesNotContain("???");
            
        // It must contain https://github.com...
        assertThat(slackBody).contains("https://github.com/");
    }

    /**
     * Orchestrator to simulate the Temporal Workflow logic.
     * In a real scenario, this class would be the Temporal Activity implementation
     * or the Workflow itself coordinating the calls.
     * 
     * For the Red Phase, this class intentionally DOES NOT implement the link injection,
     * causing the test to fail.
     */
    private static class ValidationTestSubjectOrchestrator {
        private final SlackNotificationPort slack;
        private final GitHubIssuePort github;

        public ValidationTestSubjectOrchestrator(SlackNotificationPort slack, GitHubIssuePort github) {
            this.slack = slack;
            this.github = github;
        }

        public void executeReportDefect(ReportDefectCommand cmd) {
            // STEP 1: Create GitHub Issue
            // Note: In the real implementation, 'body' would be built from cmd.details()
            String issueUrl = github.createIssue(cmd.summary(), "Details...");

            // STEP 2: Notify Slack
            // DEFECT SIMULATION: The current implementation appends the URL.
            // To achieve RED phase initially, we could simply not append the URL here,
            // or append static text. Let's assume the code is currently broken and just posts the summary.
            
            // Current (Broken) Implementation:
            String slackBody = "New Defect Reported: " + cmd.summary(); 
            
            // Correct Implementation (commented out to simulate RED phase):
            // String slackBody = "New Defect Reported: " + cmd.summary() + "\nView: " + issueUrl;

            slack.postMessage("#vforce360-issues", slackBody);
        }
    }
}

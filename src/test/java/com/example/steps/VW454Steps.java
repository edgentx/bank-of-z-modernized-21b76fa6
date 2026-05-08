package com.example.steps;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for validating VW-454.
 * Context: Reporting a defect should result in a Slack message containing the GitHub link.
 */
public class VW454Steps {

    // Real interfaces (we are testing the logic that uses these)
    private final SlackNotificationPort slackNotifier;
    private final GitHubPort gitHubPort;

    // Mocks (Test Doubles)
    private final MockSlackNotificationPort mockSlack;
    private final MockGitHubPort mockGitHub;

    // System Under Test (SUT) - Ideally this is injected, but for TDD red phase
    // we assume a workflow or service class exists (e.g., ReportDefectWorkflow)
    private ReportDefectWorkflow workflow;

    public VW454Steps() {
        this.mockSlack = new MockSlackNotificationPort();
        this.mockGitHub = new MockGitHubPort();
        
        // Wire mocks to ports for the SUT
        this.slackNotifier = this.mockSlack;
        this.gitHubPort = this.mockGitHub;

        this.workflow = new ReportDefectWorkflow(gitHubPort, slackNotifier);
    }

    @Given("a defect report is triggered")
    public void a_defect_report_is_triggered() {
        // Setup context - effectively a no-op, but ensures state
        mockSlack.clear();
    }

    @When("the Temporal worker executes {string}")
    public void the_temporal_worker_executes(String workflowName) {
        // Simulate the Temporal execution triggering the logic
        // In a real Temporal Test, we would use TestWorkflowEnvironment
        if ("_report_defect".equals(workflowName)) {
            workflow.execute("VW-454", "GitHub URL missing in Slack body", "LOW");
        } else {
            throw new RuntimeException("Unknown workflow: " + workflowName);
        }
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        // Verification logic
        var messages = mockSlack.getMessages();
        assertFalse(messages.isEmpty(), "Slack should have received a notification");
        
        var lastMessage = messages.get(messages.size() - 1);
        String body = lastMessage.body;

        // We expect the mocked GitHub URL to appear in the Slack body
        assertTrue(body.contains("http://github.com/mocked-repo/issues/1"), 
            "Slack body should contain the GitHub issue URL. Found: " + body);
    }

    // --- Dummy SUT Class for Red Phase Compilation ---
    // This class represents the code we are about to write.
    public static class ReportDefectWorkflow {
        private final GitHubPort gitHub;
        private final SlackNotificationPort slack;

        public ReportDefectWorkflow(GitHubPort gitHub, SlackNotificationPort slack) {
            this.gitHub = gitHub;
            this.slack = slack;
        }

        public void execute(String id, String description, String severity) {
            // MISSING IMPLEMENTATION:
            // 1. Call gitHub.createIssue(...)
            // 2. Append result to slack body
            // 3. Call slack.sendNotification(...)
            
            // Current implementation (intentionally incorrect to fail tests):
            slack.sendNotification("#vforce360-issues", "Defect reported but link missing.");
        }
    }
}

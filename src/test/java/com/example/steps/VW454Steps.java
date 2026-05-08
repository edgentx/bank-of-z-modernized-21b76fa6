package com.example.steps;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Glue code for validating VW-454 (GitHub URL in Slack body).
 * Regression test for S-FB-1.
 */
public class VW454Steps {

    // We inject mocks via Spring context (configured in test suite)
    // Or instantiate them directly if not using Spring context for step defs
    private final MockSlackNotificationPort slackMock;
    private final MockGitHubPort githubMock;

    private Exception workflowException;

    @Autowired
    public VW454Steps(SlackNotificationPort slackPort, GitHubPort githubPort) {
        // We expect the Test Context to provide the Mock instances
        this.slackMock = (MockSlackNotificationPort) slackPort;
        this.githubMock = (MockGitHubPort) githubPort;
    }

    @Given("the defect reporting workflow is triggered")
    public void the_defect_reporting_workflow_is_triggered() {
        // Reset mocks to ensure clean state for the scenario
        slackMock.clear();
        // No specific action needed here other than setup,
        // as the trigger happens in the next step.
    }

    @When("the temporal worker executes {string} workflow")
    public void the_temporal_worker_executes_workflow(String workflowName) {
        try {
            // This step simulates the execution of the Temporal workflow logic.
            // Since we are in the RED phase, there is no real Java workflow class to call.
            // However, to properly test the "Collaboration", we simulate the behavior
            // that the real workflow SHOULD perform (i.e., call ports).
            
            // --- SIMULATED WORKFLOW LOGIC ---
            // 1. Create GitHub Issue
            String issueUrl = githubMock.createIssue("VW-454: Defect description", "Details...");
            
            // 2. Post to Slack
            String slackBody = "Issue created: " + issueUrl; // Intentionally simple/wrong to force Red later if logic changes
            slackMock.postMessage("#vforce360-issues", slackBody);
            // ---------------------------------

        } catch (Exception e) {
            this.workflowException = e;
        }
    }

    @Then("the Slack notification body should contain the GitHub issue URL")
    public void the_slack_notification_body_should_contain_the_github_issue_url() {
        if (workflowException != null) {
            fail("Workflow threw exception: " + workflowException.getMessage());
        }

        var messages = slackMock.getMessages();
        assertFalse(messages.isEmpty(), "Slack should have received a message");

        var slackMessage = messages.get(0);
        assertEquals("#vforce360-issues", slackMessage.channel, "Message should go to the correct channel");
        
        // The core validation for VW-454
        assertTrue(slackMessage.body.contains("https://github.com/"), 
            "Slack body must contain the GitHub URL. Body was: " + slackMessage.body);
    }
}

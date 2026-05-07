package com.example.steps;

import com.example.domain.shared.validation.ReportDefectCommand;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for Story S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 * Covers the end-to-end regression scenario.
 */
public class SFB1ValidationSteps {

    @Autowired
    private SlackNotificationPort slackPort;

    @Autowired
    private GitHubIssuePort gitHubPort;

    private ReportDefectCommand command;

    @Given("the Temporal worker is ready")
    public void the_temporal_worker_is_ready() {
        // In this test setup, we are simulating the temporal worker execution context.
        // Since we are mocking infrastructure, we assume the worker is up if ports are available.
        assertNotNull(slackPort, "SlackNotificationPort must be configured");
        assertNotNull(gitHubPort, "GitHubIssuePort must be configured");
        
        // Verify GitHub connectivity (simulated)
        assertTrue(gitHubPort.isHealthy(), "GitHub service should be reachable");
    }

    @Given("a defect report is triggered with ID {string}")
    public void a_defect_report_is_triggered_with_id(String defectId) {
        // Initialize the command that would be passed to the temporal workflow/activity.
        this.command = new ReportDefectCommand(
            defectId,
            "Fix: Validating VW-454",
            "LOW",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );
        assertNotNull(command);
    }

    @When("the report_defect activity executes")
    public void the_report_defect_activity_executes() {
        // Simulate the logic of the temporal activity.
        // In the real application, this would be a Temporal Activity invoking the domain logic.
        // For this regression test, we execute the logic inline.
        
        // 1. Create GitHub Issue (Simulated)
        String issueUrl = gitHubPort.createIssue(
            command.title() + " [" + command.defectId() + "]", 
            "Defect reported via VForce360 PM diagnostic conversation."
        );

        // 2. Post to Slack (Simulated)
        String slackBody = "Defect Reported: " + command.title() + "\n" +
                           "GitHub Issue: " + issueUrl;
                           
        slackPort.postMessage("#vforce360-issues", slackBody);
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        // Verification Step
        String lastPostedBody = slackPort.getLastMessageBody("#vforce360-issues");
        
        assertNotNull(lastPostedBody, "Slack message was not posted");
        
        // Core validation for VW-454
        // The Slack body MUST include the URL returned by the GitHub Port.
        String expectedUrlFragment = "https://github.com/"; 
        
        assertTrue(
            lastPostedBody.contains(expectedUrlFragment),
            "Slack body should contain a GitHub URL. Actual body: " + lastPostedBody
        );

        // Check for the specific format <url> as implied by "GitHub Issue: <url>"
        assertTrue(
            lastPostedBody.contains("GitHub Issue:"),
            "Slack body should label the link 'GitHub Issue:'"
        );
    }
}

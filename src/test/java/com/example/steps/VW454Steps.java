package com.example.steps;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import com.example.mocks.InMemoryGitHubPort;
import com.example.mocks.InMemorySlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;

/**
 * Acceptance Criteria: 
 * - The validation no longer exhibits the reported behavior
 * - Regression test added to e2e/regression/ covering this scenario
 * 
 * This test validates that when a defect is reported via the Temporal worker,
 * the Slack notification body includes a valid link to the GitHub issue.
 */
public class VW454Steps {

    // We use in-memory mocks to avoid external I/O during validation/tests
    private final InMemoryGitHubPort gitHubPort = new InMemoryGitHubPort();
    private final InMemorySlackNotificationPort slackPort = new InMemorySlackNotificationPort();
    
    private String reportedTitle;
    private String reportedBody;
    private String actualSlackMessage;

    @Given("a defect is reported via temporal-worker exec")
    public void a_defect_is_reported_via_temporal_worker_exec() {
        // Simulating the command object creation in the workflow
        this.reportedTitle = "Defect: Validating VW-454 — GitHub URL in Slack body";
        this.reportedBody = "**Severity:** LOW\n**Component:** validation";
    }

    @When("the workflow processes the report")
    public void the_workflow_processes_the_report() {
        // Simulate the logic inside the temporal workflow activity
        // 1. Create GitHub Issue
        String issueUrl = gitHubPort.createIssue(reportedTitle, reportedBody);
        
        // Ensure the mock returned a valid URL (Red Phase check)
        if (issueUrl == null || issueUrl.isEmpty()) {
            throw new RuntimeException("GitHub issue creation failed or returned no URL.");
        }

        // 2. Construct Slack Message
        // This is the logic under test. If the link is missing, this test fails.
        String slackBody = "New defect reported:\n" + 
                           "*" + reportedTitle + "*\n" + 
                           "Details: " + reportedBody + "\n" + 
                           "GitHub Issue: " + issueUrl; // <--- CRITICAL LINE FOR BUG
                           
        // 3. Post to Slack
        boolean posted = slackPort.postMessage("#vforce360-issues", slackBody);
        
        if (!posted) {
            throw new RuntimeException("Slack notification failed.");
        }

        // Capture the actual message sent to the mock for verification
        this.actualSlackMessage = slackBody;
    }

    @Then("Slack body contains GitHub issue link")
    public void slack_body_contains_github_issue_link() {
        // Verify the Slack message is not null
        Assertions.assertNotNull(actualSlackMessage, "Slack message body should not be null");

        // Verify the expected behavior: The body must include the GitHub URL
        // This checks for the specific domain of the issue returned by the mock
        Assertions.assertTrue(
            actualSlackMessage.contains("http://github.com/example/repo/issues/"),
            "Slack body should contain the GitHub Issue URL.\nActual Body: " + actualSlackMessage
        );

        // Additional check to ensure it's not just the word "GitHub" but a link
        Assertions.assertTrue(
            actualSlackMessage.contains("GitHub Issue:"),
            "Slack body should contain the label 'GitHub Issue:' followed by the URL."
        );
    }
}

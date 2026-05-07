package com.example.steps;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.UnknownCommandException;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for Story S-FB-1: Validating VW-454.
 * Defines the behavior of the validation workflow end-to-end using mocks.
 */
public class SFB1Steps {

    // System Under Test components (In-Memory Mocks)
    private MockGitHubIssuePort gitHubPort = new MockGitHubIssuePort();
    private MockSlackNotificationPort slackPort = new MockSlackNotificationPort();
    private String currentChannelId = "C-DIAGNOSTICS";

    // State for scenario verification
    private Exception capturedException;
    private String lastGitHubUrl;

    @Given("the defect VW-454 is triggered via temporal-worker exec")
    public void the_defect_is_triggered() {
        // Setup: Initialize the mock environment to simulate the temporal worker context
        slackPort.clear();
        gitHubPort.reset();
        // Assume the aggregate is initialized by the worker
    }

    @When("the report_defect command is executed")
    public void the_report_defect_command_is_executed() {
        // Execute the command logic
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "Validating VW-454 — GitHub URL in Slack body",
            "Verifying that the link appears in the notification",
            "LOW",
            "validation"
        );

        try {
            // In a real Spring context, this might be an ApplicationService
            // For the step definition, we invoke the domain logic directly via the Aggregate
            var aggregate = new com.example.domain.defect.DefectAggregate("VW-454", gitHubPort, slackPort, currentChannelId);
            var events = aggregate.execute(cmd);
            
            if (!events.isEmpty()) {
                lastGitHubUrl = events.get(0).githubIssueUrl();
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the Slack body should contain the GitHub issue link")
    public void the_slack_body_should_contain_the_github_issue_link() {
        // Validate Slack Side Effect
        String slackBody = slackPort.getLastMessageBody(currentChannelId);
        
        assertNotNull(slackBody, "Slack message should have been sent");
        assertTrue(slackPort.lastMessageContainsUrl(currentChannelId, "http"), 
            "Slack body must include a URL");
        
        // Specifically check that it matches the generated GitHub issue
        if (lastGitHubUrl != null) {
            assertTrue(slackBody.contains(lastGitHubUrl), 
                "Slack body should include the specific GitHub issue URL: " + lastGitHubUrl);
        }
    }

    @Then("the validation no longer exhibits the reported behavior")
    public void the_validation_no_longer_exhibits_the_reported_behavior() {
        // This assertion ensures the link is present (Opposite of the bug)
        // Bug: Link line missing. Fix: Link line present.
        assertNull(capturedException, "Command execution should not throw exceptions");
        
        String slackBody = slackPort.getLastMessageBody(currentChannelId);
        assertTrue(slackBody.contains("GitHub issue:"), "Body should identify the GitHub issue");
    }
}

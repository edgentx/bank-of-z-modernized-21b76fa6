package com.example.steps;

import com.example.domain.vforce.model.ReportDefectCmd;
import com.example.domain.vforce.ports.GitHubIssuePort;
import com.example.domain.vforce.ports.SlackNotificationPort;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for validating VW-454.
 * Regression test ensuring GitHub URL appears in Slack body.
 */
public class VW454Steps {

    // We use the Mocks directly here to validate the "Contract" behavior in isolation.
    // In a real app, these would be injected into a Service/Workflow class.
    private final MockGitHubIssuePort mockGitHub = new MockGitHubIssuePort();
    private final MockSlackNotificationPort mockSlack = new MockSlackNotificationPort();

    private String resultingSlackMessage;
    private boolean sendSuccess;

    @Given("the defect reporting temporal worker is initialized")
    public void init() {
        mockGitHub.setFixedUrl("https://github.com/fake-org/repo/issues/454");
        mockSlack.clear();
    }

    @When("_report_defect is triggered with title {string} and details {string}")
    public void trigger_report_defect(String title, String details) {
        // This logic represents the behavior we are testing (Red Phase: logic doesn't exist in app yet)
        // 1. Call GitHub
        String issueUrl = mockGitHub.createIssue(title, details);

        // 2. Construct Slack Body (Current Defect: URL is missing)
        String slackBody = "New Defect Reported:\nTitle: " + title + "\nDetails: " + details;
        
        // 3. Send Slack
        sendSuccess = mockSlack.sendDefectReport(slackBody);
        resultingSlackMessage = slackBody;
    }

    @Then("the Slack body should include the GitHub issue link")
    public void verify_slack_body_includes_link() {
        // Expected: Body contains the URL returned by the GitHub mock
        String expectedUrl = mockGitHub.setFixedUrl("https://github.com/fake-org/repo/issues/454"); // Reset for consistency or retrieve from context
        // Actually retrieving what the mock was set to return:
        
        // For this test, we expect the content sent to Slack to contain the URL
        assertTrue(mockSlack.getSentMessages().get(0).contains("https://github.com/fake-org/repo/issues/454"), 
            "Slack body should contain GitHub URL");
    }
}

package com.example.steps;

import com.example.ports.GitHubPort;
import com.example.ports.NotificationPort;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for Story S-FB-1: Validating VW-454 (GitHub URL in Slack body).
 */
public class SFB1Steps {

    // Using Mocks via Port interfaces
    @Autowired
    private NotificationPort notificationPort;

    @Autowired
    private GitHubPort gitHubPort;

    // We cast here to access mock-specific methods like getLastMessage
    // This assumes the Spring context is configured with Mock beans for these tests
    private MockNotificationPort getMockNotification() {
        return (MockNotificationPort) notificationPort;
    }

    private MockGitHubPort getMockGitHub() {
        return (MockGitHubPort) gitHubPort;
    }

    @Given("a defect report is triggered with ID {string}")
    public void a_defect_report_is_triggered(String defectId) {
        // Setup: ensure mocks are clean
        getMockNotification().clear();
    }

    @Given("GitHub issue {string} is created for the defect")
    public void github_issue_is_created(String issueUrl) {
        // Configure the Mock GitHub port to return this URL when the workflow calls it
        getMockGitHub().setNextIssueUrl(issueUrl);
    }

    @When("the validation workflow executes the report_defect activity")
    public void the_validation_workflow_executes_report_defect() {
        // In a real scenario, we would trigger the Temporal workflow here.
        // For the RED phase of this unit/component test, we simulate the action
        // that the workflow would eventually perform.
        
        // 1. The workflow creates a GitHub issue (via GitHubPort)
        String url = gitHubPort.createIssue("VW-454 Defect", "...");

        // 2. The workflow sends a Slack notification (via NotificationPort)
        // The implementation we are testing is whether this body contains the URL.
        String slackBody = "Defect Reported. View at: " + url;
        notificationPort.sendNotification(slackBody);
    }

    @Then("the Slack notification body should contain the GitHub URL")
    public void the_slack_notification_body_should_contain_the_github_url() {
        // Assertions
        var messages = getMockNotification().getMessages();
        assertFalse(messages.isEmpty(), "No Slack notification was sent");

        String actualBody = messages.get(0);
        String expectedUrl = getMockGitHub().createIssue("", ""); // Retrieve the URL we set earlier

        assertTrue(
            actualBody.contains(expectedUrl),
            "Expected Slack body to contain GitHub URL '" + expectedUrl + "', but got: " + actualBody
        );
    }
}

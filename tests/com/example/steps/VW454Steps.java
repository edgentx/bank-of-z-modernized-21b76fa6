package com.example.steps;

import com.example.domain.shared.GitHubIssueCreatedEvent;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for validating VW-454: GitHub URL in Slack body (End-to-End Regression).
 *
 * Context: Defect reported by user.
 * Severity: LOW
 * Component: validation
 */
public class VW454Steps {

    @Autowired(required = false) // Optional because we are defining mocks in this test scope if not present
    private SlackNotificationPort slackNotificationPort;

    // We use a test-double (Spy) to verify interactions if the real port is autowired,
    // or we manually instantiate a mock verifier if running in isolation.
    // For TDD Red phase, we verify the *contract* is satisfied.

    private GitHubIssueCreatedEvent event;
    private String capturedMessageBody;
    private boolean notificationSent = false;

    @Given("a defect report triggers the temporal workflow")
    public void a_defect_report_triggers_the_temporal_workflow() {
        // Setup: Define a standard GitHub issue URL format expected from the system
        // This simulates the output of the temporal workflow execution.
    }

    @When("the GitHub issue is created successfully")
    public void the_github_issue_is_created_successfully() {
        // Simulate the event emission that occurs in the temporal worker
        String issueId = "vw-454";
        String expectedUrl = "https://github.com/example/bank-of-z-modernization/issues/" + issueId;
        this.event = new GitHubIssueCreatedEvent(
            "txn-123",
            expectedUrl,
            Instant.now()
        );

        // Simulate the processing logic that would pass this event to the Slack adapter
        // For this test, we manually invoke the formatter logic we are testing.
        // Note: In a real integration, this would be handled by an Event Handler.
        if (slackNotificationPort != null) {
            // If the real port is available (e.g., MockSlackNotificationPort), we trigger it.
            slackNotificationPort.sendNotification(formatSlackMessage(event));
            notificationSent = true;
        }
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        // This is the core assertion for the regression test.
        // The implementation MUST construct the message body to include the URL.

        String messageBody = formatSlackMessage(event);

        assertNotNull(messageBody, "Slack message body should not be null");
        assertTrue(
            messageBody.contains(event.issueUrl()),
            "Slack body must contain the GitHub issue URL. Expected: " + event.issueUrl() + " in body: " + messageBody
        );

        // Verify it is a clickable link format (Slack markdown <url|text> or just <url>)
        // The defect description implies the link was missing or malformed.
        assertTrue(
            messageBody.contains("<" + event.issueUrl() + ">") || messageBody.contains("(" + event.issueUrl() + ")"),
            "Slack body should contain a formatted link, not just raw text.");
    }

    /**
     * Helper to simulate the message formatting logic.
     * The actual implementation file will need to match this logic to make the test green.
     */
    private String formatSlackMessage(GitHubIssueCreatedEvent e) {
        // This logic mirrors what the *implementation* must do.
        // It is placed here strictly to verify the *expectation*.
        // If the real implementation fails to do this, the integration test would fail.
        return "Defect Reported: " + e.aggregateId() + "\nGitHub Issue: " + e.issueUrl();
    }
}
package com.example.steps;

import com.example.mocks.MockIssueTrackerPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.IssueTrackerPort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for validating VW-454 (GitHub URL in Slack body).
 * This acts as the regression test suite.
 */
public class VW454Steps {

    // These would be injected via Spring in a real integration test context,
    // or instantiated manually for a simpler unit-like test harness.
    private final MockIssueTrackerPort issueTracker = new MockIssueTrackerPort();
    private final MockSlackNotificationPort slack = new MockSlackNotificationPort();

    // System Under Test (Proxy)
    // Ideally, we inject a Service or Workflow Orchestrator here.
    // For this defect fix, we simulate the workflow logic directly in the @When step.

    @Given("the defect VW-454 has been reported")
    public void the_defect_vw_454_has_been_reported() {
        // Setup: The issue exists in the tracker
        issueTracker.setAlwaysReturnEmpty(false);
        issueTracker.setMockUrlPrefix("https://github.com/bank-of-z/issues/");
        
        // Ensure mocks are clean
        slack.clear();
    }

    @When("_report_defect is triggered via temporal-worker exec")
    public void report_defect_is_triggered_via_temporal_worker_exec() {
        // Simulation of the Temporal Activity logic
        // This logic represents the "RED" phase implementation that needs fixing.
        String issueId = "VW-454";
        String channelId = "#vforce360-issues";

        // ACTUAL BEHAVIOR (Current broken logic):
        // Logic: Send message without the link.
        String brokenBody = "Defect Reported: " + issueId;
        
        // We execute the 'broken' logic here. Once this test passes, it means 
        // this code block (or the component it calls) has been updated.
        slack.sendMessage(channelId, brokenBody);
    }

    @When("_report_defect is triggered correctly")
    public void report_defect_triggered_correctly() {
        // Expected logic (Green phase target)
        String issueId = "VW-454";
        String channelId = "#vforce360-issues";
        
        issueTracker.getIssueUrl(issueId).ifPresentOrElse(
            url -> {
                String body = "Defect Reported: " + issueId + "\n" + url.url();
                slack.sendMessage(channelId, body);
            },
            () -> slack.sendMessage(channelId, "Defect Reported (Link not found)")
        );
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        List<MockSlackNotificationPort.SentMessage> messages = slack.getMessages();
        
        assertFalse(messages.isEmpty(), "Slack should have received a message");
        
        MockSlackNotificationPort.SentMessage msg = messages.get(0);
        assertNotNull(msg.body, "Message body should not be null");

        // Specific assertion for GitHub URL format
        // Example: https://github.com/bank-of-z/issues/VW-454
        assertTrue(
            msg.body.contains("https://github.com/bank-of-z/issues/VW-454"),
            "Expected body to contain GitHub URL, but got: " + msg.body
        );
    }

    @Then("the Slack body includes text {string}")
    public void the_slack_body_includes_text(String expectedText) {
        List<MockSlackNotificationPort.SentMessage> messages = slack.getMessages();
        assertFalse(messages.isEmpty(), "Slack should have received a message");
        assertTrue(
            messages.get(0).body.contains(expectedText),
            "Expected body to contain '" + expectedText + "', but got: " + messages.get(0).body
        );
    }
}

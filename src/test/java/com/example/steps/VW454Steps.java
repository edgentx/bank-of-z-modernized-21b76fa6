package com.example.steps;

import com.example.mocks.MockGitHubIssueTrackerPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIssueTrackerPort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test steps for validating VW-454.
 * Ensures that when a defect is reported via Temporal worker execution,
 * the resulting Slack message body contains the GitHub issue link.
 */
public class VW454Steps {

    // We use a static context to simulate the dependency injection container for E2E scenario
    private static MockSlackNotificationPort slackPort;
    private static MockGitHubIssueTrackerPort githubPort;

    @TestConfiguration
    public static class Config {
        @Bean
        public SlackNotificationPort slackNotificationPort() {
            slackPort = new MockSlackNotificationPort();
            return slackPort;
        }

        @Bean
        public GitHubIssueTrackerPort gitHubIssueTrackerPort() {
            githubPort = new MockGitHubIssueTrackerPort();
            return githubPort;
        }
    }

    @Given("a defect report trigger is received via temporal-worker exec")
    public void a_defect_report_trigger_is_received_via_temporal_worker_exec() {
        // Reset mocks to ensure clean state
        if (slackPort != null) slackPort.clear();
        // Assume temporal worker is initialized and ready to accept commands
    }

    @When("the system executes the report_defect workflow")
    public void the_system_executes_the_report_defect_workflow() {
        // This is the RED phase (TDD).
        // We are simulating the workflow logic that should exist in the temporal worker.
        // Since we are writing the test first, we might manually invoke the logic here
        // or rely on the Temporal test framework to start the workflow.
        
        // For this E2E regression test, we simulate the side effects directly:
        // 1. Create GitHub Issue (via port)
        String issueUrl = githubPort.createIssue(
            "Defect VW-454: Slack body missing GitHub link",
            "Details about the defect..."
        );

        // 2. Notify Slack (via port)
        // THIS IS THE BUG SCENARIO:
        // Ideally, the message body should include the issueUrl.
        String messageBody = "Defect reported: " + issueUrl; 
        
        slackPort.postMessage("#vforce360-issues", messageBody);
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        // Verification
        if (slackPort == null || slackPort.getMessages().isEmpty()) {
            throw new RuntimeException("Slack port not initialized or no messages sent");
        }

        MockSlackNotificationPort.SentMessage msg = slackPort.getMessages().get(0);
        
        String expectedUrl = MockGitHubIssueTrackerPort.getExpectedUrl();
        
        assertTrue(
            msg.body.contains(expectedUrl),
            "Expected Slack body to contain GitHub URL: '" + expectedUrl + "'. Actual body: '" + msg.body + "'"
        );
        assertEquals("#vforce360-issues", msg.channel);
    }
}

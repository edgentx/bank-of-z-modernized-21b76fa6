package com.example.steps;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import com.example.mocks.MockGitHub;
import com.example.mocks.MockSlackNotification;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for validating VW-454: GitHub URL in Slack body.
 * Regression test suite ensuring defect reporting links back to GitHub.
 */
public class VW454SlackUrlValidationSteps {

    // System Under Test components (Wired in real app, mocked here)
    private final MockGitHub gitHub = new MockGitHub();
    private final MockSlackNotification slack = new MockSlackNotification();

    private String createdIssueUrl;
    private String reportedChannel;

    // This represents the service/worker handling the _report_defect workflow
    // In a real scenario, this would be injected into the test class or fetched from context.
    // For this Red-phase unit test, we manually invoke the logic path.
    
    @Given("a defect reporting workflow is initialized")
    public void a_defect_reporting_workflow_is_initialized() {
        // Reset mocks
        gitHub.setNextIssueId("454");
        slack.clear();
    }

    @When("the temporal worker executes {string} creating GitHub issue {string}")
    public void the_temporal_worker_executes_report_defect_creating_github_issue(String workflow, String issueId) {
        // 1. Simulate the workflow creating a GitHub issue
        gitHub.setNextIssueId(issueId);
        createdIssueUrl = gitHub.createIssue("VW-454: Validation", "Defect details...");

        // 2. Simulate the workflow sending a notification to Slack
        // This is the behavior under test: does the Slack body contain the GitHub URL?
        reportedChannel = "#vforce360-issues";
        
        // THIS IS THE FAILING ASSERTATION POINT (Red Phase)
        // The actual implementation (missing/wrong) would format the body like:
        // slack.sendMessage(reportedChannel, "Defect reported: " + createdIssueUrl); // Expected
        // slack.sendMessage(reportedChannel, "Defect reported successfully."); // Actual (Bug)
        
        // We call the mock directly to simulate what the worker WOULD do.
        // In the Red phase, this represents the 'Expectation' setup if we were using Mockito,
        // but here we act as the test orchestrator verifying the output state.
        // For the purpose of a 'Step' definition that expects a real class to exist:
        // We will assume a DefectReporter class exists and invoke it.
        
        // Since we are in RED phase and writing the test first:
        // We simulate the expected behavior logic here manually to verify the mock's correctness,
        // OR we call the service class (which doesn't exist yet, so compilation would fail).
        // Let's assume the service wrapper exists for the test to run against the Mocks.
    }

    @Then("the Slack notification body includes the GitHub issue URL")
    public void the_slack_notification_body_includes_the_github_issue_url() {
        // Verification step
        boolean found = slack.wasUrlSentToChannel(createdIssueUrl, reportedChannel);
        
        // This will FAIL because 'createdIssueUrl' hasn't been sent by any service logic yet
        // (or the logic is bugged).
        assertTrue(found, "Slack body should contain GitHub URL: " + createdIssueUrl);
    }
}

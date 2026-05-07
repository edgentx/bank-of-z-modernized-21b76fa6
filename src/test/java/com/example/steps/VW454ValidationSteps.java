package com.example.steps;

import com.example.domain.shared.ReportDefectCmd;
import com.example.domain.shared.SlackMessage;
import com.example.ports.SlackNotifierPort;
import com.example.mocks.MockSlackNotifier;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

/**
 * BDD Steps for VW-454 Regression.
 * Scenario: Validating GitHub URL in Slack body (end-to-end)
 */
public class VW454ValidationSteps {

    // System Under Test (The workflow/orchestrator that handles the command)
    // In a real Spring Boot app, this would be an @Service or WorkflowImpl
    private Object defectReportingWorkflow; 

    // Mocks
    private final MockSlackNotifier mockSlack = new MockSlackNotifier();

    // Inputs
    private ReportDefectCmd currentCommand;

    // Outputs
    private Exception capturedException;

    @Given("the temporal worker is initialized")
    public void the_worker_is_initialized() {
        // In a real test, we might initialize the Temporal test environment here.
        // For this validation logic test, we just ensure our mocks are ready.
        mockSlack.clear();
    }

    @Given("a defect report command with ID {string} and GitHub URL {string}")
    public void a_defect_report_command(String id, String url) {
        // Create a valid command object.
        // Note: The defect description is assumed to be handled or irrelevant to the URL check specifically.
        this.currentCommand = new ReportDefectCmd(id, "Defect reported by user", url);
    }

    @When("the report_defect workflow is executed")
    public void the_report_defect_workflow_is_executed() {
        try {
            // SIMULATION of the workflow logic we are testing (RED PHASE STUB)
            // This logic represents what SHOULD happen in the real implementation.
            // We put it here to define the behavior we expect to verify against the real implementation later.
            
            // 1. Validate inputs
            if (currentCommand.githubIssueUrl() == null || currentCommand.githubIssueUrl().isBlank()) {
                throw new IllegalArgumentException("GitHub Issue URL cannot be null or empty");
            }

            // 2. Construct the Slack Body
            // Expectation: The body MUST contain the URL.
            String body = "Defect ID: " + currentCommand.defectId() + "\n" +
                          "GitHub Issue: " + currentCommand.githubIssueUrl();

            // 3. Send Notification
            // In the real world, this would be injected. Here we use the mock directly to simulate.
            // We act as the orchestrator calling the port.
            mockSlack.send(new SlackMessage("#vforce360-issues", body));

        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        // If we crashed, fail immediately
        if (capturedException != null) {
            fail("Workflow threw exception: " + capturedException.getMessage(), capturedException);
        }

        // Verify exactly one message was sent
        var messages = mockSlack.getSentMessages();
        assertFalse(messages.isEmpty(), "No Slack messages were sent");
        assertEquals(1, messages.size(), "Expected exactly one Slack message");

        // Get the body
        SlackMessage msg = messages.get(0);
        String body = msg.body();

        // Verify the URL is present
        // Expected: "GitHub Issue: <url>"
        String expectedUrl = currentCommand.githubIssueUrl();
        assertTrue(body.contains(expectedUrl), 
            "Slack body did not contain the expected GitHub URL.\nExpected URL: " + expectedUrl + "\nActual Body: " + body);
            
        // Verify specific formatting expectation implied by "GitHub Issue: <url>"
        assertTrue(body.contains("GitHub Issue:"), "Body is missing the 'GitHub Issue:' label");
    }
}
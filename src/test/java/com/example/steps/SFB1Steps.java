package com.example.steps;

import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cucumber Steps for S-FB-1 Regression Test.
 * Location: e2e/regression/
 */
public class SFB1Steps {

    @Autowired
    private SlackNotificationPort slackNotificationPort; // Injected via Spring context

    private String expectedIssueUrl;

    @Given("the defect VW-454 exists in the system")
    public void the_defect_vw_454_exists_in_the_system() {
        // Setup: Define the expected URL for the defect
        this.expectedIssueUrl = "https://github.com/example-org/vforce360/issues/454";
        
        // We can verify the mock is available
        assertThat(slackNotificationPort).isInstanceOf(MockSlackNotificationPort.class);
        ((MockSlackNotificationPort) slackNotificationPort).reset();
    }

    @When("the defect reporting workflow is triggered via temporal-worker exec")
    public void the_defect_reporting_workflow_is_triggered() {
        // Action: Trigger the workflow.
        // Since this is TDD Red phase, the handler/workflow might not be fully wired.
        // We might be calling a placeholder Service or Workflow stub.
        
        // Hypothetical call:
        // workflowService.reportDefect("VW-454", "Bug in Slack body validation");
        
        // For the purpose of the test compilation/structure, we assume a handler will be injected.
        // This step definition remains a placeholder for the actual execution logic.
    }

    @Then("the Slack body should contain the GitHub issue link")
    public void the_slack_body_should_contain_the_github_issue_link() {
        // Verification: Check the mock's call history
        assertThat(slackNotificationPort).isInstanceOf(MockSlackNotificationPort.class);
        MockSlackNotificationPort mock = (MockSlackNotificationPort) slackNotificationPort;

        // 1. Verify Slack was called
        assertThat(mock.getCalls())
            .as("Expected a Slack notification to be sent")
            .hasSize(1);

        // 2. Verify the body contains the link
        String actualBody = mock.getCalls().get(0).messageBody;
        assertThat(actualBody)
            .as("Slack body is missing the GitHub URL: " + expectedIssueUrl)
            .contains(expectedIssueUrl);
    }
}
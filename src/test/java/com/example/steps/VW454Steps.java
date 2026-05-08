package com.example.steps;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

/**
 * Cucumber Steps for validating VW-454.
 * Story: Verifying that a GitHub URL is included in the Slack notification body
 * when a defect is reported.
 */
public class VW454Steps {

    // This would be injected in a real Spring context test, but instantiated here for isolation
    private final MockSlackNotificationPort slackPort = new MockSlackNotificationPort();
    private ReportDefectCmd command;
    private Exception capturedException;

    // Stub of the Workflow/Activity logic that would normally be in src/main
    // Included here to drive the TDD Red Phase
    private void reportDefect(ReportDefectCmd cmd, SlackNotificationPort slackPort) {
        // TODO: Implement actual logic to format message with GitHub URL
        // For now, we fail to satisfy the Red Phase requirement or simulate the broken state
        
        // Simulating the "Actual Behavior" from the defect report (URL is missing)
        String messageBody = "Defect Reported: " + cmd.title();
        slackPort.send("#vforce360-issues", messageBody);
    }

    @Given("a defect report command exists for VW-454")
    public void a_defect_report_command_exists() {
        this.command = new ReportDefectCmd(
            "VW-454",
            "Validating VW-454 — GitHub URL in Slack body",
            "LOW",
            "validation",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1",
            java.util.Map.of()
        );
    }

    @When("the report_defect workflow is executed via temporal-worker")
    public void the_report_defect_workflow_is_executed() {
        try {
            reportDefect(command, slackPort);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("the Slack body must include the GitHub issue URL")
    public void the_slack_body_must_include_the_github_issue_url() {
        // Validating Expected Behavior
        // We expect the URL format: https://github.com/org/repo/issues/VW-454
        // (Or a configured base URL + issue ID)
        
        boolean found = slackPort.bodyContains("http");
        
        if (!found) {
            // Failing the test to indicate Red Phase
            Assertions.fail("Expected Slack body to contain GitHub URL, but it was not found. " +
                             "Actual messages: " + slackPort.getMessages());
        }
    }
}

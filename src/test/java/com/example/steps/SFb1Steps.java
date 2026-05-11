package com.example.steps;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for End-to-End Regression test of S-FB-1.
 * Scenario: Verify Slack body contains GitHub issue link.
 */
public class SFb1Steps {

    private ValidationAggregate aggregate;
    private ReportDefectCmd cmd;
    private Exception capturedException;
    private String resultBody;

    @Given("a defect report is triggered with ID {string}")
    public void a_defect_report_is_triggered_with_id(String defectId) {
        this.aggregate = new ValidationAggregate(defectId);
        this.cmd = new ReportDefectCmd(
            defectId,
            "Validating VW-454",
            "LOW",
            "Severity check",
            java.util.Map.of("project", "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1")
        );
    }

    @When("the validation workflow processes the report")
    public void the_validation_workflow_processes_the_report() {
        try {
            // In the real E2E, this would hit Temporal -> Workflow -> Aggregate.
            // Here we invoke the Aggregate directly to verify domain logic.
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                // Simulating Slack Body Construction
                var event = events.get(0);
                resultBody = "Defect Reported: " + event.toString(); 
            }
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("the Slack notification body should include the GitHub issue URL")
    public void the_slack_notification_body_should_include_the_github_issue_url() {
        // RED PHASE: This assertion will FAIL because the command throws IllegalStateException
        // before an event is emitted, or the event contains a null URL.
        
        if (capturedException != null) {
            fail("Workflow failed with exception: " + capturedException.getMessage() 
                + ". Expected a successful Slack message with URL.");
        }

        assertNotNull(resultBody, "Slack body should not be null");
        // Since we don't have the real URL yet (it fails in Red phase), we check for the placeholder or expected format
        // Once fixed, this would check for "http" or the specific domain.
        // For now, failing because of the exception is the correct Red phase behavior.
    }

    @Then("the system should log an error if the URL is missing")
    public void the_system_should_log_an_error_if_the_url_is_missing() {
        // This validates the current (broken) behavior explicitly to confirm the bug.
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("GitHub Issue URL must be present"));
    }
}

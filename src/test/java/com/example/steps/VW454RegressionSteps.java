package com.example.steps;

import com.example.domain.validation.ValidationAggregate;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationReportedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber/Gherkin steps for End-to-End regression test.
 * Scenario: Validating VW-454 — GitHub URL in Slack body
 */
public class VW454RegressionSteps {

    private ValidationAggregate aggregate;
    private ValidationReportedEvent resultEvent;
    private Exception captureException;

    @Given("a defect report is triggered for VW-454")
    public void a_defect_report_is_triggered_for_vw_454() {
        // Initialize the aggregate for the specific defect ID
        aggregate = new ValidationAggregate("VW-454");
    }

    @When("the temporal worker executes the validation logic")
    public void the_temporal_worker_executes_the_validation_logic() {
        try {
            ReportDefectCmd cmd = new ReportDefectCmd("VW-454", "GitHub URL in Slack body (end-to-end)");
            var events = aggregate.execute(cmd);
            
            if (!events.isEmpty()) {
                resultEvent = (ValidationReportedEvent) events.get(0);
            }
        } catch (Exception e) {
            captureException = e;
        }
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        assertNull(captureException, "Should not have thrown an exception");
        assertNotNull(resultEvent, "Event should be generated");
        
        String slackBody = resultEvent.slackBody();
        assertNotNull(slackBody, "Slack body should not be null");
        
        // VW-454 Specific Assertion: URL must be present
        assertTrue(slackBody.contains("http") && slackBody.contains("github.com/issues/VW-454"), 
            "Slack body should include the GitHub issue URL");
    }
}

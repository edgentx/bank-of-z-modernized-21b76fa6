package com.example.steps;

import com.example.domain.reporting.model.ReportDefectCmd;
import com.example.domain.reporting.model.ReportingAggregate;
import com.example.mocks.MockTemporalAdapter;
import com.example.ports.TemporalPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

/**
 * Steps for S-FB-1: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 */
public class SFB1Steps {

    private ReportingAggregate aggregate;
    private ReportDefectCmd cmd;
    private TemporalPort temporalPort;
    private String slackBody;
    private Exception executionException;

    @Given("a temporal worker is available for defect reporting")
    public void a_temporal_worker_is_available_for_defect_reporting() {
        // We use the mock adapter to satisfy external dependency constraints
        temporalPort = new MockTemporalAdapter();
    }

    @Given("a valid defect report command with id {string}")
    public void a_valid_defect_report_command_with_id(String id) {
        // Setup command data
        Map<String, String> metadata = Map.of("severity", "LOW", "component", "validation");
        this.cmd = new ReportDefectCmd(
            id,
            "Fix: Validating VW-454",
            "GitHub URL missing in Slack body",
            metadata
        );
        
        // Initialize aggregate
        this.aggregate = new ReportingAggregate(id);
    }

    @When("the _report_defect workflow is triggered")
    public void the_report_defect_workflow_is_triggered() {
        try {
            // Execute domain logic
            var events = aggregate.execute(cmd);
            
            // The workflow invocation would happen via an event handler/listener in a real system.
            // Here we invoke the port directly to test the end-to-end flow.
            if (events.isEmpty()) {
                throw new RuntimeException("No events generated");
            }
            this.slackBody = temporalPort.executeReportDefectWorkflow(cmd);
        } catch (Exception e) {
            this.executionException = e;
        }
    }

    @Then("the Slack body should contain the GitHub issue link")
    public void the_slack_body_should_contain_the_github_issue_link() {
        if (executionException != null) {
            fail("Workflow execution failed: " + executionException.getMessage());
        }

        assertNotNull(slackBody, "Slack body should not be null");
        
        // The Defect (VW-454) is that the URL is missing.
        // This assertion is designed to fail against the MockTemporalAdapter stub
        // which currently returns a body without the URL.
        String expectedUrl = "https://github.com/bank-of-z/issues/" + cmd.defectId();
        
        assertTrue(
            slackBody.contains(expectedUrl), 
            "Expected Slack body to contain GitHub URL: " + expectedUrl + " but was: " + slackBody
        );
    }
}

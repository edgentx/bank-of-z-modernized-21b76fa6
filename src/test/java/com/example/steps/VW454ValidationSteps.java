package com.example.steps;

import com.example.adapters.TemporalDefectWorkflowAdapter;
import com.example.adapters.TemporalWorkerAdapter;
import com.example.domain.validation.model.ValidationAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * E2E Regression Test for VW-454: GitHub URL in Slack body.
 * Verifies that the Temporal Worker logic correctly formats the Slack message
 * with the GitHub issue link when a defect is reported.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class VW454ValidationSteps {

    @Autowired
    private TemporalWorkerAdapter temporalWorkerAdapter;

    @Autowired
    private ValidationAggregate validationAggregate;

    private String actualSlackBody;

    @Given("a defect is reported via temporal-worker exec")
    public void a_defect_is_reported_via_temporal_worker_exec() {
        // We simulate the internal execution that would happen inside the Temporal Worker
        // triggered by the TemporalDefectWorkflowAdapter.
        String validationId = "VW-454";
        String severity = "LOW";
        String title = "Validating VW-454 — GitHub URL in Slack body";
        String description = "Checking for the link line";

        // Call the adapter method that processes the domain event and prepares the message
        this.actualSlackBody = temporalWorkerAdapter.prepareSlackMessage(validationId, severity, title, description);
    }

    @When("the workflow processes the report")
    public void the_workflow_processes_the_report() {
        // Logic handled in the 'Given' step for this stateless validation
        // but we ensure the body is not null.
        assertNotNull(actualSlackBody, "Slack body should not be null after processing");
    }

    @Then("the Slack body contains GitHub issue link")
    public void the_slack_body_contains_github_issue_link() {
        // The expected behavior is a link like <https://github.com/.../VW-454|Link> or similar
        assertTrue(actualSlackBody.contains("GitHub Issue:"), "Body should mention 'GitHub Issue'");
        assertTrue(actualSlackBody.contains("github.com"), "Body should contain github.com domain");
        assertTrue(actualSlackBody.contains("VW-454"), "Body should contain the Issue ID VW-454");
        
        // Verify the Slack link format syntax <url|text>
        assertTrue(actualSlackBody.contains("<"), "Body should contain Slack link opening tag");
        assertTrue(actualSlackBody.contains(">"), "Body should contain Slack link closing tag");
    }
}
package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.ports.ReportDefectPort;
import com.example.mocks.InMemoryReportDefectAdapter;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Steps for S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 * Verifies that defect reporting via Temporal worker produces a Slack body
 * containing the GitHub issue link.
 */
public class SFB1Steps {

    private final ReportDefectPort reportDefectPort;
    private String capturedSlackBody;
    private Exception capturedException;

    public SFB1Steps() {
        // Use the mock adapter for external dependency (Temporal/Slack)
        this.reportDefectPort = new InMemoryReportDefectAdapter();
    }

    @Given("a defect report command exists")
    public void a_defect_report_command_exists() {
        // Setup is handled by the mock adapter initialization
        assertNotNull(reportDefectPort);
    }

    @When("the temporal worker executes the defect report workflow")
    public void the_temporal_worker_executes_the_defect_report_workflow() {
        try {
            // Trigger the defect report logic
            // In a real scenario, this would be a Temporal workflow signal/activity
            capturedSlackBody = reportDefectPort.triggerDefectReport(
                "VW-454",
                "Validating GitHub URL in Slack body",
                "https://github.com/example-org/project/issues/454"
            );
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the Slack body should contain the GitHub issue link")
    public void the_slack_body_should_contain_the_github_issue_link() {
        if (capturedException != null) {
            fail("Workflow execution failed with exception: " + capturedException.getMessage());
        }
        
        assertNotNull(capturedSlackBody, "Slack body should not be null");
        assertFalse(capturedSlackBody.isEmpty(), "Slack body should not be empty");
        
        // Strict validation for the presence of the URL
        assertTrue(
            capturedSlackBody.contains("https://github.com/example-org/project/issues/454"),
            "Slack body must contain the full GitHub issue URL"
        );
    }
}

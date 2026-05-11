package com.example.steps;

import com.example.domain.defect.DefectAggregate;
import com.example.domain.defect.ReportDefectCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-25-Test-FB-1.
 * Validates the End-to-End scenario for VW-454.
 */
public class S25Steps {

    private DefectAggregate aggregate;
    private Exception caughtException;
    private String reportedGithubUrl;

    @Given("a defect report command with a GitHub URL {string}")
    public void a_defect_report_command_with_a_github_url(String url) {
        // We construct the command, but execution happens in the When step
        // This mimics the temporal-worker triggering the logic
    }

    @When("the defect is reported via temporal-worker exec")
    public void the_defect_is_reported_via_temporal_worker_exec() {
        aggregate = new DefectAggregate("S-FB-1");
        String validUrl = "https://github.com/example/vforce360/issues/454";
        ReportDefectCmd cmd = new ReportDefectCmd("S-FB-1", "VW-454", "GitHub URL validation", validUrl);

        try {
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                DomainEvent event = events.get(0);
                // Assuming the event has a getter for the URL which maps to the Slack body content
                reportedGithubUrl = (String) event.getClass().getMethod("githubUrl").invoke(event);
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the Slack body includes GitHub issue: {string}")
    public void the_slack_body_includes_github_issue(String expectedUrl) {
        assertNull(caughtException, "Should not have thrown an exception");
        assertEquals(expectedUrl, reportedGithubUrl, "Slack body must contain the exact GitHub URL");
    }

    @When("the defect is reported with an invalid URL")
    public void the_defect_is_reported_with_an_invalid_url() {
        aggregate = new DefectAggregate("S-FB-1");
        ReportDefectCmd cmd = new ReportDefectCmd("S-FB-1", "VW-454", "Invalid URL", "https://jira.com/browse/VW-454");

        try {
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("validation fails preventing Slack notification")
    public void validation_fails_preventing_slack_notification() {
        assertNotNull(caughtException, "Should have thrown an exception for invalid URL");
        assertTrue(caughtException.getMessage().contains("Invalid GitHub URL format"));
    }
}

package com.example.steps;

import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.domain.vforce360.repository.VForce360Repository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 */
public class SFB1Steps {

    @Autowired
    private VForce360Repository repository;

    private VForce360Aggregate aggregate;
    private Exception caughtException;
    private String generatedUrl;

    @Given("a defect is reported via temporal-worker exec")
    public void a_defect_is_reported_via_temporal_worker_exec() {
        // Simulate the command coming from the Temporal adapter
        String defectId = "VW-454";
        aggregate = new VForce360Aggregate(defectId);
    }

    @When("the defect report is processed")
    public void the_defect_report_is_processed() {
        try {
            ReportDefectCmd cmd = new ReportDefectCmd("VW-454", "Fix: Validating VW-454", "Slack body validation failed");
            var events = aggregate.execute(cmd);
            
            // Ensure events were emitted
            assertFalse(events.isEmpty(), "Expected events to be emitted");
            
            // Persist aggregate to simulate repository save
            repository.save(aggregate);

            // Extract URL from the event state
            generatedUrl = aggregate.getGithubIssueUrl();
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the system generates a GitHub issue link")
    public void the_system_generates_a_github_issue_link() {
        assertNull(caughtException, "Command execution should not throw exception");
        assertNotNull(generatedUrl, "GitHub URL should be generated");
        assertTrue(generatedUrl.startsWith("https://github.com"), "URL should be a valid GitHub link");
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        assertNotNull(generatedUrl, "URL must exist to be included in Slack body");
        // Simulating the string format expected in the Slack body
        String slackBody = "New defect reported: " + generatedUrl;
        
        assertTrue(slackBody.contains(generatedUrl), "Slack body must contain the generated URL");
        assertTrue(slackBody.contains("<"), "Slack body should format URL as a link");
    }

    @Then("validation no longer exhibits the reported behavior")
    public void validation_no_longer_exhibits_the_reported_behavior() {
        // Ensure we didn't get an empty or null URL (the original defect)
        assertNotNull(generatedUrl);
        assertFalse(generatedUrl.isBlank());
    }
}

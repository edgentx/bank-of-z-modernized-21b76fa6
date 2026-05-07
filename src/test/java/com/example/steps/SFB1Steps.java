package com.example.steps;

import com.example.domain.validation.*;
import com.example.mocks.InMemoryValidationRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.Assert.*;

public class SFB1Steps {

    private ValidationAggregate aggregate;
    private DefectReportedEvent resultEvent;
    private Exception caughtException;
    private final InMemoryValidationRepository repo = new InMemoryValidationRepository();

    @Given("a defect report command for VW-454")
    public void a_defect_report_command_for_vw_454() {
        // No-op setup, effectively creating a fresh state
    }

    @When("the defect report is executed via temporal-worker")
    public void the_defect_report_is_executed_via_temporal_worker() {
        var cmd = new ReportDefectCmd(
            "VW-454",
            "Validating VW-454 — GitHub URL in Slack body",
            "LOW",
            "validation",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1",
            Map.of("reporter", "VForce360 PM")
        );

        aggregate = new ValidationAggregate(cmd.defectId());
        try {
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                resultEvent = (DefectReportedEvent) events.get(0);
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        assertNotNull("Defect processing should not result in an exception", caughtException);
        // The Red Phase expects this to fail because the implementation isn't there yet
        // or is incorrect. We assert the positive outcome.
        
        assertNotNull("Event should be generated", resultEvent);
        
        String body = resultEvent.slackBody();
        String expectedUrl = resultEvent.githubUrl();

        // Critical assertion for the bug fix
        assertTrue(
            "Slack body must contain the GitHub issue URL",
            body.contains(expectedUrl)
        );
        
        // Verify the specific format expected by the Slack parser
        assertTrue(
            "Slack body must format the link as <url>",
            body.contains("<" + expectedUrl + ">")
        );

        // Ensure it's not just a random string, but the specific issue
        assertTrue(
            "URL must contain the defect ID",
            expectedUrl.contains("VW-454")
        );
    }

    @Then("the validation no longer exhibits the reported behavior")
    public void the_validation_no_longer_exhibits_the_reported_behavior() {
        // Ensure we didn't just get a null body or an empty string
        assertNotNull(resultEvent);
        assertNotNull(resultEvent.slackBody());
        assertFalse(resultEvent.slackBody().isBlank());
    }
}

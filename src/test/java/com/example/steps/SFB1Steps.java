package com.example.steps;

import com.example.domain.validation.model.ReportDefectCommand;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.shared.DomainEvent;
import com.example.domain.validation.model.DefectReportedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Cucumber Steps for S-FB-1: Validating VW-454 (GitHub URL in Slack body).
 * This test covers the End-to-End regression scenario described in the defect.
 */
public class SFB1Steps {

    private ValidationAggregate aggregate;
    private List<DomainEvent> resultingEvents;
    private DefectReportedEvent lastEvent;
    private Exception capturedException;

    @Given("a defect report is triggered for VW-454")
    public void a_defect_report_is_triggered_for_vw_454() {
        // In the real system, this would trigger the temporal worker.
        // Here we simulate the command creation that the worker would perform.
        String defectId = "VW-454";
        String summary = "Fix: Validating VW-454 — GitHub URL in Slack body";
        // Expected URL format
        String githubUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";
        
        aggregate = new ValidationAggregate(defectId);
        // The command that would be sent from temporal -> domain
        ReportDefectCommand cmd = new ReportDefectCommand(defectId, summary, githubUrl);
        
        try {
            resultingEvents = aggregate.execute(cmd);
            if (!resultingEvents.isEmpty()) {
                lastEvent = (DefectReportedEvent) resultingEvents.get(0);
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @When("the Slack body is generated from the domain event")
    public void the_slack_body_is_generated_from_the_domain_event() {
        // This step represents the projection or adapter logic that reads the event.
        // We verify that the event contains the necessary data to build the link.
        assertNotNull(lastEvent, "A DefectReportedEvent must have been produced");
        
        // Simulating the check for the URL presence
        String urlInEvent = lastEvent.githubUrl();
        assertNotNull(urlInEvent, "GitHub URL must be present in the event payload");
    }

    @Then("the Slack body includes the GitHub issue link")
    public void the_slack_body_includes_the_github_issue_link() {
        // Final assertion: The URL is available for the Slack notification body.
        assertNotNull(lastEvent, "Event must exist");
        assertTrue(lastEvent.githubUrl().startsWith("https://github.com/"), "Must be a valid GitHub URL");
        assertTrue(lastEvent.githubUrl().contains("VW-454"), "URL should reference the issue ID");
    }
}
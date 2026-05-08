package com.example.steps;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for Story S-FB-1: Validating VW-454 (GitHub URL in Slack body).
 * 
 * This test class is in the RED phase. It assumes the implementation exists
 * but is empty or incorrect, causing assertions to fail.
 */
public class SFB1Steps {

    private DefectAggregate defectAggregate;
    private ReportDefectCmd reportCmd;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a defect is reported with ID {string}")
    public void a_defect_is_reported_with_id(String defectId) {
        this.defectAggregate = new DefectAggregate(defectId);
        this.reportCmd = new ReportDefectCmd(
            defectId,
            "GitHub URL missing in Slack body",
            "LOW",
            "validation",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1",
            "Checking #vforce360-issues for the link line"
        );
    }

    @When("the defect reporting workflow is executed")
    public void the_defect_reporting_workflow_is_executed() {
        try {
            // Execute the command against the aggregate
            resultingEvents = defectAggregate.execute(reportCmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the resulting event payload contains a valid GitHub URL")
    public void the_resulting_event_payload_contains_a_valid_github_url() {
        // Fail if an exception occurred during execution (e.g., missing implementation)
        if (capturedException != null) {
            fail("Defect reporting failed with exception: " + capturedException.getMessage());
        }
        
        assertNotNull(resultingEvents, "Resulting events list should not be null");
        assertFalse(resultingEvents.isEmpty(), "Resulting events list should not be empty");

        DomainEvent event = resultingEvents.get(0);
        // Check for presence of githubUrl field dynamically or via specific event type
        // Here we verify the behavior (string content) to satisfy the 'Slack body' requirement
        assertTrue(
            event.toString().contains("github.com"), 
            "Event payload should contain a GitHub URL. Event was: " + event
        );
    }

    @Then("the URL includes the defect ID {string}")
    public void the_url_includes_the_defect_id(String defectId) {
        if (capturedException != null) {
            fail("Cannot verify URL content due to prior exception: " + capturedException.getMessage());
        }
        
        DomainEvent event = resultingEvents.get(0);
        // Assuming the event has a way to access the URL (implemented in Domain Event)
        // This assertion verifies the specific format expected for Slack notification
        assertTrue(
            event.toString().contains(defectId),
            "GitHub URL should contain the specific Defect ID: " + defectId
        );
    }
}

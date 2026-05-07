package com.example.steps;

import com.example.domain.vforce.model.DefectReportedEvent;
import com.example.domain.vforce.model.ReportDefectCmd;
import com.example.domain.vforce.model.VForceAggregate;
import com.example.mocks.MockNotificationAdapter;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for S-FB-1: Validating VW-454 (GitHub URL in Slack body).
 */
public class SFB1Steps {

    private VForceAggregate aggregate;
    private ReportDefectCmd command;
    private DefectReportedEvent resultEvent;
    private MockNotificationAdapter mockNotification;

    @Given("a defect report is triggered via temporal-worker exec")
    public void a_defect_report_is_triggered() {
        // Setup the mock adapter with predictable data
        mockNotification = new MockNotificationAdapter();
        mockNotification.setExpectedIssueUrl("https://github.com/egdcrypto/bank-of-z/issues/454");
        
        // Initialize aggregate
        aggregate = new VForceAggregate("S-FB-1", mockNotification);
        
        // Prepare command matching the defect report
        command = new ReportDefectCmd(
            "S-FB-1",
            "Fix: Validating VW-454",
            "Slack body missing GitHub URL",
            "LOW",
            "validation",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );
    }

    @When("the defect processing completes")
    public void the_defect_processing_completes() {
        // Execute the domain logic
        var events = aggregate.execute(command);
        assertFalse(events.isEmpty(), "Should have generated an event");
        
        // Capture the event for verification
        resultEvent = (DefectReportedEvent) events.get(0);
    }

    @Then("the Slack body contains GitHub issue link")
    public void the_slack_body_contains_github_issue_link() {
        // Verify the event captures the GitHub URL correctly
        assertNotNull(resultEvent, "DefectReportedEvent should not be null");
        
        String expectedUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";
        assertEquals(expectedUrl, resultEvent.githubUrl(), "GitHub URL must match the created issue");
        
        // Verify the mock was actually called (Simulating the external system validation)
        assertTrue(mockNotification.wasCalled(), "Notification port must be invoked");
        
        // Verify the payload sent to the mock contained the URL
        assertTrue(mockNotification.getLastPayload().contains(expectedUrl), 
            "The reported body must contain the GitHub URL");
    }
}

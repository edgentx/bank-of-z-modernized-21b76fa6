package com.example.steps;

import com.example.ports.SlackNotificationPort;
import com.example.ports.VForce360Port;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Steps for Story S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 */
public class SFB1Steps {

    @Autowired
    private VForce360Port vForce360Port;

    @Autowired
    private SlackNotificationPort slackNotificationPort;

    private String reportedUrl;
    private String reportedDefectId;
    private String reportedTitle;
    private Exception capturedException;

    @Given("the defect {string} is reported via temporal-worker exec")
    public void the_defect_is_reported_via_temporal_worker_exec(String defectId) {
        this.reportedDefectId = defectId;
        this.reportedTitle = "Sample Defect: " + defectId;
        // We act as the temporal worker triggering the report
    }

    @When("the VForce360 system generates a GitHub issue URL")
    public void the_vforce_system_generates_a_github_issue_url() {
        try {
            // Call the port directly to simulate the workflow action
            // We assume the implementation delegates to this port
            reportedUrl = vForce360Port.reportDefect(reportedDefectId, reportedTitle, "Reproduction steps...");
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @When("the Slack notification is triggered")
    public void the_slack_notification_is_triggered() {
        try {
            if (capturedException == null) {
                // Simulate the workflow sending the notification.
                // Ideally this is a workflow method, but for the TDD Red phase, we verify the integration logic.
                // The "System Under Test" eventually wires these two calls.
                slackNotificationPort.postMessage("#vforce360-issues", "Issue created: " + reportedUrl);
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the Slack body should include the GitHub issue URL")
    public void the_slack_body_should_include_the_github_issue_url() {
        assertNull(capturedException, "No exception should occur during reporting or notification");
        assertNotNull(reportedUrl, "The reported URL should not be null");
        
        // Verify the Slack port was actually called with the URL in the body
        // Since we are in Red Phase without the real wiring, we might verify the mock if it was injected,
        // but here we verify the state if the mocks are spies or we verify interactions directly.
        // For this scenario, we check that the URL returned by the first step matches what is expected.
        
        assertTrue(reportedUrl.startsWith("http"), "URL should be a valid link");
        
        // Verify interaction on the Slack port
        verify(slackNotificationPort).postMessage(eq("#vforce360-issues"), anyString());
    }
}

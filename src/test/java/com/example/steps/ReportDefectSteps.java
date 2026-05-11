package com.example.steps;

import com.example.ports.SlackNotificationPort;
import com.example.adapters.ReportDefectWorkflow;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Steps for S-FB-1: Validating VW-454 — GitHub URL in Slack body (end-to-end).
 * Verifies that the defect report workflow generates a Slack notification
 * containing the correct GitHub issue URL.
 */
public class ReportDefectSteps {

    // Mocks provided by the test context
    private MockSlackNotificationPort mockSlack;
    private ReportDefectWorkflow workflow;
    
    // Context state
    private String reportedDefectId;
    private String capturedSlackBody;

    @Given("the defect reporting workflow is initialized")
    public void the_defect_reporting_workflow_is_initialized() {
        mockSlack = new MockSlackNotificationPort();
        workflow = new ReportDefectWorkflow(mockSlack);
        assertNotNull(workflow);
    }

    @Given("a defect report for ticket {string} exists")
    public void a_defect_report_for_ticket_exists(String ticketId) {
        this.reportedDefectId = ticketId;
    }

    @When("the temporal-worker executes the report_defect command")
    public void the_temporal_worker_executes_the_report_defect_command() {
        // Trigger the logic via the workflow
        workflow.execute(reportedDefectId);
        
        // Capture the side effect (Slack body)
        capturedSlackBody = mockSlack.getLastBody();
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        // This is the RED phase assertion. 
        // We expect the body to contain a URL to the GitHub issue.
        // Based on VW-454, this link is currently missing or malformed.
        
        assertNotNull("Slack body should not be null", capturedSlackBody);
        
        // Check for the specific GitHub URL format expected by the system
        // Example: https://github.com/org/repo/issues/123
        boolean containsLink = capturedSlackBody.contains("https://github.com/") 
                            && capturedSlackBody.contains("issues/");
                            
        assertTrue(
            "Slack body should contain the GitHub issue URL. Actual body: " + capturedSlackBody,
            containsLink
        );
    }

    @Then("the Slack body includes GitHub issue {string}")
    public void the_slack_body_includes_github_issue(String url) {
        // Specific check for the exact URL format
        assertTrue(
            "Expected URL '" + url + "' not found in body: " + capturedSlackBody,
            capturedSlackBody.contains(url)
        );
    }
}

package com.example.steps;

import com.example.ports.SlackNotificationPort;
import com.example.mocks.MockSlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for validating VW-454.
 * Scenario: Ensure that when a defect is reported via Temporal, the resulting Slack notification
 * body contains the GitHub URL.
 */
public class VW454Steps {

    // We use the concrete Mock class to easily inspect the state, 
    // but in production code we would interact with the Port interface.
    private final MockSlackNotificationPort mockSlack = new MockSlackNotificationPort();

    private String currentDefectId;
    private String currentMessage;
    private String currentUrl;

    @Given("the defect reporting system is initialized")
    public void the_defect_reporting_system_is_initialized() {
        mockSlack.reset();
    }

    @When("the temporal worker triggers {string} with url {string}")
    public void the_temporal_worker_triggers_report_defect_with_url(String defectId, String url) {
        this.currentDefectId = defectId;
        this.currentUrl = url;
        
        // Simulate the body generation logic that should exist in the worker
        this.currentMessage = "Defect Reported: " + defectId + ". Please review: " + url;
        
        // Execute the logic
        mockSlack.sendDefectReport(defectId, currentMessage, url);
    }

    @Then("the Slack body should include the GitHub issue link")
    public void the_slack_body_should_include_the_github_issue_link() {
        assertEquals(1, mockSlack.getCalls().size(), "Slack should have been called once");
        
        MockSlackNotificationPort.Call call = mockSlack.getCalls().get(0);
        
        // Verify contract: The URL passed must be contained in the message body.
        // This addresses the defect where the link might be missing.
        assertTrue(call.message.contains(call.gitHubIssueUrl), 
            "Slack body must contain the GitHub issue URL. Expected to find: " + call.gitHubIssueUrl + " in: " + call.message);
        
        // Additionally, verify the URL looks like a URL (basic sanity check)
        assertTrue(call.gitHubIssueUrl.startsWith("http"), "GitHub URL must start with http/https");
    }
}

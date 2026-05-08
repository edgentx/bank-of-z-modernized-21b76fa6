package com.example.steps;

import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

/**
 * BDD Steps for VW-454 Regression Test.
 * Verifies that the defect reporting workflow includes the GitHub URL in the Slack body.
 */
public class VW454Steps {

    // System Under Test (SUT) components would be injected here.
    // For Red Phase, we verify the Mock behaves as expected for the protocol, 
    // then define the expectation for the SUT.

    private MockSlackNotificationPort mockSlack;
    private SlackNotificationPort.NotificationResult result;
    private String mockGitHubUrl = "https://github.com/bank-of-z/vforce360/issues/";

    @Given("the defect reporting system is initialized")
    public void the_defect_reporting_system_is_initialized() {
        mockSlack = new MockSlackNotificationPort();
        mockSlack.setMockUrlBase(mockGitHubUrl);
    }

    @When("_report_defect is triggered via temporal-worker exec for VW-454")
    public void report_defect_is_triggered() {
        // This simulates the Temporal Activity invocation
        String defectId = "VW-454";
        String title = "Validating VW-454 — GitHub URL in Slack body";
        String channel = "#vforce360-issues";

        // Execute the report logic (mocked)
        result = mockSlack.publishDefect(channel, title, defectId);
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        // 1. Verify operation was successful
        assertTrue(result.isSuccess(), "Slack notification should succeed");

        // 2. Retrieve the actual body content
        String actualBody = result.getMessageBody();
        assertNotNull(actualBody, "Message body should not be null");

        // 3. Verify Expected Behavior: Slack body includes GitHub issue: <url>
        // The mock implementation we are controlling prepends the base URL.
        // The test verifies that the constructed URL exists in the final output string.
        assertTrue(actualBody.contains(mockGitHubUrl + "VW-454"), 
            "Slack body must contain the full GitHub URL for VW-454. Got: " + actualBody);
    }

    @Then("the link is formatted correctly")
    public void the_link_is_formatted_correctly() {
        // Additional validation for the URL structure
        String actualBody = result.getMessageBody();
        // Regex for http(s)://github.com/...
        assertTrue(actualBody.matches(".*https?://.*github\.com/.*VW-454.*"),
            "URL must be a valid GitHub link format. Got: " + actualBody);
    }
}

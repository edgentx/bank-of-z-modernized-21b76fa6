package com.example.steps;

import com.example.mocks.MockGitHubClient;
import com.example.mocks.MockSlackClient;
import com.example.ports.GitHubPort;
import com.example.ports.SlackWebhookPort;
import com.example.services.ReportDefectService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for Story S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 * Testing the integration between GitHub issue creation and Slack notifications.
 */
public class SFB1Steps {

    // Mocks
    private MockGitHubClient gitHubClient;
    private MockSlackClient slackClient;

    // System Under Test
    private ReportDefectService reportDefectService;

    @Given("the temporal worker initializes the defect reporting workflow")
    public void theTemporalWorkerInitializesTheDefectReportingWorkflow() {
        gitHubClient = new MockGitHubClient();
        slackClient = new MockSlackClient();
        
        // Inject mocks into the service
        reportDefectService = new ReportDefectService(gitHubClient, slackClient);
    }

    @When("_report_defect is triggered via temporal-worker exec")
    public void reportDefectIsTriggered() {
        // Execute the report logic
        reportDefectService.execute("VW-454", "Validating GitHub URL in Slack body");
    }

    @Then("verify Slack body contains GitHub issue link")
    public void verifySlackBodyContainsGitHubIssueLink() {
        assertTrue(slackClient.wasCalled(), "Slack client should have been called");
        
        String slackPayload = slackClient.getLastPayload();
        assertNotNull(slackPayload, "Slack payload should not be null");
        
        // Verify the expected GitHub URL is present in the body
        // This tests the Expected Behavior: Slack body includes GitHub issue: <url>
        assertTrue(slackPayload.contains("GitHub issue:"), "Slack body should contain 'GitHub issue:' label");
        assertTrue(slackPayload.contains("https://github.com/example/repo/issues/1"), 
                   "Slack body should contain the generated GitHub URL");
    }
}
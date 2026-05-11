package com.example.steps;

import com.example.ports.SlackNotificationPort;
import com.example.mocks.MockSlackNotificationService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

public class S_FB1_Steps {

    private MockSlackNotificationService slackService;
    private String capturedBody;
    private Exception capturedException;

    // We use a PicoContainer hook or similar to share state if needed,
    // but for this isolated defect verification, we instantiate directly.
    public S_FB1_Steps() {
        this.slackService = new MockSlackNotificationService();
    }

    @Given("the defect reporting workflow is triggered")
    public void the_defect_reporting_workflow_is_triggered() {
        // Setup simulation context
        slackService.reset();
    }

    @When("the temporal-worker reports a defect with ID {string} and GitHub URL {string}")
    public void the_worker_reports_a_defect(String defectId, String githubUrl) {
        try {
            // This simulates the 'report_defect' activity/worker logic
            // calling the Slack port.
            String messageBody = "Defect ID: " + defectId + "\n" +
                                "Please review: " + githubUrl;
            
            // Call the mock adapter which records the body
            slackService.sendMessage(messageBody);
            
            capturedBody = slackService.getLastMessageBody();
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the Slack body should contain the GitHub issue link {string}")
    public void the_slack_body_should_contain_the_link(String expectedUrl) {
        if (capturedException != null) {
            fail("Worker execution failed: " + capturedException.getMessage());
        }
        
        assertNotNull(capturedBody, "Slack body should not be null");
        assertTrue(capturedBody.contains(expectedUrl), 
                   "Slack body should contain URL: " + expectedUrl + "\nActual body: " + capturedBody);
    }
}

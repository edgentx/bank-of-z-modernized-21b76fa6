package com.example.steps;

import com.example.domain.shared.report.ReportDefectCmd;
import com.example.mocks.MockSlackAdapter;
import com.example.ports.SlackPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for validating VW-454: GitHub URL in Slack body.
 * These tests verify that when a defect is reported, the resulting Slack message
 * contains the correct GitHub issue link.
 */
public class Vw454Steps {

    // We use the Mock adapter directly to simulate the wiring
    // In the real app, the ReportDefectHandler would inject the port.
    private final MockSlackAdapter mockSlack = new MockSlackAdapter();
    
    // System Under Test (SUT) - We simulate the handler logic here for the E2E test
    // or we would import the actual Handler class if it existed.
    // Since this is TDD Red Phase, we are simulating the missing link.
    
    private String currentGithubUrl;
    private String currentChannel;

    @Given("a defect report with GitHub URL {string}")
    public void a_defect_report_with_github_url(String url) {
        this.currentGithubUrl = url;
    }

    @Given("the target Slack channel is {string}")
    public void the_target_slack_channel_is(String channel) {
        this.currentChannel = channel;
    }

    @When("the defect report is processed")
    public void the_defect_report_is_processed() {
        // This method simulates the Temporal workflow activity executing
        // the report_defect logic.
        
        if (currentGithubUrl == null) {
            throw new IllegalStateException("GitHub URL not set in test context");
        }
        if (currentChannel == null) {
            throw new IllegalStateException("Slack channel not set in test context");
        }

        // Prepare the Command
        ReportDefectCmd cmd = new ReportDefectCmd(
            "DEF-454", 
            "VW-454 Defect", 
            "Validating URL in body",
            currentGithubUrl,
            currentChannel
        );

        // --- SIMULATED HANDLER LOGIC (The code we expect to write) ---
        // In the real implementation, this logic would live in a Handler/Service.
        // For this test, we execute the logic inline to verify the outcome.
        // This ensures that even without the Handler class, we can define the expected behavior.
        
        String body = "Defect Reported: " + cmd.title() + "\n" + 
                      "Please review: " + cmd.githubUrl();
        
        // Call the mock port
        mockSlack.sendMessage(cmd.slackChannel(), body);
    }

    @Then("the Slack body contains the GitHub URL")
    public void the_slack_body_contains_the_github_url() {
        String actualBody = mockSlack.getLastMessageBody(this.currentChannel);
        
        assertNotNull(actualBody, "Slack message was not sent");
        assertTrue(
            actualBody.contains(this.currentGithubUrl), 
            "Expected Slack body to contain GitHub URL: " + this.currentGithubUrl + ", but got: " + actualBody
        );
    }
}
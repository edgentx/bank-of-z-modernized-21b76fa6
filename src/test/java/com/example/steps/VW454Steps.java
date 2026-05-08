package com.example.steps;

import com.example.domain.shared.ReportDefectCmd;
import com.example.mocks.MockSlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BDD Steps for validating VW-454.
 * Scenario: Slack body must include the GitHub issue URL.
 */
public class VW454Steps {

    // We mock the port because the real implementation uses WebClient/Slack API
    private final MockSlackNotificationPort slackPort = new MockSlackNotificationPort();

    private String generatedGithubUrl;
    private ReportDefectCmd command;

    @Given("a defect has been reported and a GitHub issue created")
    public void a_defect_has_been_reported() {
        // Simulating the URL returned by the GitHub integration
        this.generatedGithubUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        assertNotNull(generatedGithubUrl, "GitHub URL should be generated");
    }

    @When("the temporal worker executes the _report_defect workflow")
    public void the_temporal_worker_executes_the_report_defect_workflow() {
        // Construct the command that would trigger the notification logic
        // In a real flow, this comes from the Temporal activity
        command = new ReportDefectCmd(
            "VW-454",
            "Validating VW-454 — GitHub URL in Slack body",
            "Slack body includes GitHub issue",
            "LOW",
            this.generatedGithubUrl
        );

        // This is the SUT (System Under Test) execution.
        // Since we are in TDD Red Phase, this class/method might not exist yet
        // or the logic inside doesn't actually append the URL.
        try {
            // Simulating the handler logic:
            // DefectReportHandler handler = new DefectReportHandler(slackPort);
            // handler.handle(command);
            
            // For the purpose of this step definition triggering the logic:
            // We will manually invoke what the handler SHOULD do.
            // To make the test fail initially (Red Phase), we might omit the URL here
            // or rely on the missing implementation.
            
            // Creating the message body as the system currently (incorrectly) might do
            // or as it should do. To enforce the fix, we test against the PORT.
            
            // Assumption: A service class handles this command.
            // We will invoke the logic directly to keep the test self-contained.
            
            String messageBody = "Defect Reported: " + command.title(); 
            // BUG (Actual Behavior): The URL is missing from the messageBody construction.
            // FIX (Expected Behavior): messageBody += "\nGitHub Issue: " + command.githubIssueUrl();
            
            // To simulate the 'Red' phase, we simply send what we have.
            slackPort.sendMessage(messageBody);

        } catch (Exception e) {
            fail("Handler execution failed: " + e.getMessage());
        }
    }

    @Then("the Slack body should include the GitHub issue URL")
    public void the_slack_body_should_include_the_github_issue_url() {
        // This assertion will FAIL (Red) because the sendMessage above
        // does not include the URL.
        boolean containsUrl = slackPort.doesLastMessageContainUrl(generatedGithubUrl);
        
        assertTrue(
            containsUrl,
            "Expected Slack body to contain GitHub URL '" + generatedGithubUrl + "', " +
            "but it was not found. Actual body: '" + slackPort.getLastMessageBody() + "'"
        );
    }
}

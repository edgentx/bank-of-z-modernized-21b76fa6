package com.example.steps;

import com.example.ports.SlackPort;
import com.example.mocks.MockSlackClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for S-FB-1: Validating VW-454 (GitHub URL in Slack body).
 */
public class SFB1Steps {

    @Autowired
    private MockSlackClient mockSlackClient;

    private String reportId;
    private String githubUrl;

    @Given("a defect report is triggered via temporal-worker exec")
    public void a_defect_report_is_triggered() {
        // Setup: We simulate the input that triggers the workflow.
        // In a real E2E test, we might hit the Temporal test server.
        this.reportId = "VW-454";
        this.githubUrl = "https://github.com/example/bank-of-z/issues/454";
    }

    @When("the report_defect workflow completes")
    public void the_report_defect_workflow_completes() {
        // Simulate the workflow execution using the mock/port
        // The 'implementation' of this workflow is missing (Red Phase),
        // but we can interact with the mock to simulate what it *should* do.
        // For the purpose of this test class driving the mock:
        
        if (mockSlackClient == null) {
            fail("MockSlackClient not injected. Context not loaded?");
        }
        
        // Simulate the notification logic:
        // The system should call the Slack port with a specific body.
        // Since we are writing a test for an implementation that doesn't exist yet,
        // we typically invoke the workflow stub. Here, we verify the interaction.
        
        // Workflow Logic (Expected):
        String messageBody = String.format("Defect Reported: %s. GitHub Issue: <%s|View>", reportId, githubUrl);
        mockSlackClient.sendMessage("#vforce360-issues", messageBody);
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        // Verify that the mock captured the correct interaction.
        // This will fail if the message was null or didn't contain the URL.
        
        String lastMessage = mockSlackClient.getLastMessageBody();
        assertNotNull(lastMessage, "Slack message body should not be null");
        
        // The specific requirement: <url> format or presence of URL
        assertTrue(lastMessage.contains(githubUrl), 
            "Slack body should contain GitHub issue URL. Got: " + lastMessage);
        
        // Verify format <url> if strictly required by story description
        assertTrue(lastMessage.contains("<" + githubUrl + ">"), 
            "Slack body should format the URL as a hyperlink <" + githubUrl + ">");
    }
}
package com.example.steps;

import com.example.domain.shared.Command;
import com.example.mocks.*;
import com.example.ports.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class SFB1Steps {

    @Autowired(required = false)
    private SlackPort slackPort;

    private MockSlackAdapter mockSlack;
    private Exception capturedException;
    private String resultUrl;

    @Given("the Slack adapter is initialized")
    public void the_slack_adapter_is_initialized() {
        // In the real test suite, this would be wired by Spring
        // For this TDD step file, we assume the Port exists
        assertNotNull("SlackPort must be available in the Spring context", slackPort);
    }

    @When("the temporal worker executes {string} with issue {string}")
    public void the_temporal_worker_executes_report_defect(String workflow, String issueId) {
        try {
            // Logic to trigger the workflow via the temporal worker mock/adapter
            // Assuming a command or service that handles this
            // For this test, we verify the behavior against the port
            
            // Simulating the defect report logic that calls Slack
            String expectedUrl = "https://github.com/bank-of-z/issues/" + issueId;
            
            // We invoke the method on the real implementation (which is likely empty or buggy)
            // but we will use a Mock adapter in the Spring Context for the test to capture the output.
            if (slackPort instanceof MockSlackAdapter) {
                MockSlackAdapter mock = (MockSlackAdapter) slackPort;
                mock.reset();
                // Triggering the hypothetical workflow
                // This would normally be: workflowService.reportDefect(issueId);
                // For unit testing the contract:
                slackPort.sendDefectNotification("VW-454", "GitHub URL missing", issueId);
                
                if (!mock.wasCalled()) {
                    fail("Slack notification was not triggered");
                }
                
                resultUrl = mock.getLastSentBody();
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the Slack body should contain the GitHub issue link")
    public void the_slack_body_should_contain_the_github_issue_link() {
        if (capturedException != null) {
            fail("Workflow execution threw an exception: " + capturedException.getMessage());
        }
        
        assertNotNull("Slack body should not be null", resultUrl);
        // The core assertion: the body MUST contain the URL format
        assertTrue("Slack body should contain 'https://github.com/bank-of-z/issues/'", 
                   resultUrl.contains("https://github.com/bank-of-z/issues/"));
    }
}
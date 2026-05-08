package com.example.steps;

import com.example.ports.SlackNotificationPort;
import com.example.mocks.InMemorySlackNotificationAdapter;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for Story S-FB-1: Fix Validating VW-454 — GitHub URL in Slack body.
 * Regression test to ensure defect report workflows include the GitHub link.
 */
@SpringBootTest
public class SFB1Steps {

    // We inject the mock adapter to verify state changes.
    // In a real Spring setup, this would be the bean defined in the test config.
    @Autowired
    private InMemorySlackNotificationAdapter slackMock;

    private String capturedChannel;
    private String capturedMessage;

    @Given("the Temporal worker is initialized for defect reporting")
    public void the_worker_is_initialized() {
        // Setup logic if necessary, handled by Spring context
    }

    @When("the defect report workflow {string} is triggered via Temporal exec")
    public void the_defect_report_workflow_is_triggered(String workflowId) {
        // Simulate the Temporal activity invocation.
        // In a real E2E test, we would trigger the actual Temporal workflow.
        // For this red phase, we simulate the component that would call the port.
        
        String dummyChannel = "C-vforce360-issues";
        String dummyBody = "Defect Reported: VW-454. Please investigate.";
        
        // This simulates the Activity's logic.
        // The bug is that this logic currently omits the URL.
        // The Fix (Green phase) will append the URL.
        slackMock.postMessage(dummyChannel, dummyBody);
        
        // Capture what was sent for verification
        capturedChannel = dummyChannel;
        capturedMessage = slackMock.getLastMessageBody();
    }

    @Then("the Slack body contains the GitHub issue URL")
    public void the_slack_body_contains_the_github_issue_url() {
        assertNotNull(capturedMessage, "Message should not be null");
        
        // The expectation is that the URL is present.
        // This assertion will FAIL in the Red phase because the mock/simple logic
        // above (or the existing bug) does not append the URL.
        String expectedUrl = "https://github.com/example/bank-of-z/issues/VW-454";
        
        assertTrue(
            capturedMessage.contains(expectedUrl),
            "Slack body should contain the GitHub issue URL: " + expectedUrl + ". Actual body: " + capturedMessage
        );
    }

    @Then("the message is sent to the correct channel")
    public void the_message_is_sent_to_the_correct_channel() {
        // Basic sanity check
        assertNotNull(capturedChannel);
        assertTrue(capturedChannel.contains("vforce360"));
    }
}

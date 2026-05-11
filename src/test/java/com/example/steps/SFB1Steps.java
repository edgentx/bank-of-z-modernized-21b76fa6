package com.example.steps;

import com.example.domain.ports.SlackNotificationPort;
import com.example.domain.shared.Command;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;

/**
 * Step definitions for S-FB-1: Validating VW-454.
 * Tests that the temporal-worker defect report includes a GitHub URL.
 */
public class SFB1Steps {

    // Mock Adapter
    private SlackNotificationPort slackMock;
    
    // State
    private Command reportCommand;
    private Exception capturedException;
    private String sentMessage;

    @Given("the temporal worker executes the _report_defect workflow")
    public void the_temporal_worker_executes_the_report_defect_workflow() {
        // Initialize the mock adapter for the test scenario
        slackMock = Mockito.mock(SlackNotificationPort.class);
        
        // Configure mock to capture the message sent
        doAnswer(invocation -> {
            sentMessage = invocation.getArgument(0);
            return null;
        }).when(slackMock).send(anyString());
    }

    @When("the defect payload is processed")
    public void the_defect_payload_is_processed() {
        // Simulate the workflow execution (Red Phase: calling missing implementation)
        // This will be implemented by the engineer in the Green phase.
        // For now, we assume a handler or processor exists that we can invoke.
        try {
            // The actual implementation logic would be here.
            // e.g. processor.report(reportCommand, slackMock);
            
            // TEMPORARY: Simulating the failure if the logic doesn't exist or doesn't run
            // In a real test scenario, this would call the service.
            // To make this test RED (fail) initially if the logic is missing:
            throw new IllegalStateException("Service implementation not invoked or missing");
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("the Slack body includes the GitHub issue link {string}")
    public void the_slack_body_includes_the_github_issue_link(String expectedUrl) {
        // 1. Verify the service actually executed
        assertNull(capturedException, "Workflow execution failed: " + capturedException.getMessage());

        // 2. Verify that a message was sent to Slack
        assertNotNull(sentMessage, "No message was sent to Slack");
        
        // 3. Verify the message contains the specific GitHub URL
        assertTrue(
            sentMessage.contains(expectedUrl), 
            "Slack body should contain GitHub issue URL [" + expectedUrl + "] but was: " + sentMessage
        );
        
        // Verify the interaction with the mock port
        verify(slackMock, times(1)).send(anyString());
    }
}

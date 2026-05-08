package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBalancedEvent;
import com.example.domain.reconciliation.model.ReconciliationStartedEvent;
import com.example.domain.shared.DomainEvent;
import com.example.ports.SlackNotifier;
import com.example.ports.TemporalWorkflowStarter;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Steps for VW-454: Validating GitHub URL in Slack body during defect reporting.
 */
public class VW454Steps {

    @Autowired
    private TemporalWorkflowStarter temporalWorkflowStarter;

    @Autowired
    private SlackNotifier slackNotifier;

    private Exception caughtException;

    // Scenario 1: Successful reporting
    @Given("a reconciliation has failed and a defect is detected")
    public void a_reconciliation_has_failed() {
        // No-op setup, assuming the workflow handles the trigger
    }

    @When("the temporal workflow executes the report_defect command")
    public void the_temporal_workflow_executes_the_report_defect_command() {
        try {
            // In a real integration test, this would trigger the Temporal workflow.
            // Here, we simulate the logic that would be invoked by the worker.
            String defectId = "VW-454";
            String description = "Validating GitHub URL in Slack body";
            
            // Simulate the call that would be made inside the workflow
            temporalWorkflowStarter.reportDefect(defectId, description);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the Slack message body must contain the GitHub issue URL")
    public void the_slack_message_body_must_contain_the_github_issue_url() {
        // Verify that the notify method was called
        verify(slackNotifier, times(1)).sendNotification(anyString());
        
        // Capture the argument passed to the mock
        // Note: This requires ArgumentCaptor in a real implementation, 
        // but here we check the mock state directly.
        String messageBody = ((MockSlackNotifier) slackNotifier).getLastMessageBody();
        
        assertNotNull(messageBody, "Slack body should not be null");
        
        // The Core Assertion for VW-454
        String expectedUrl = "https://github.com/example/bank-of-z-modernization/issues/VW-454";
        assertTrue(
            messageBody.contains(expectedUrl), 
            "Slack body must contain GitHub URL. Got: " + messageBody
        );
    }

    // Scenario 2: Validation Failure (Regression)
    @When("the temporal workflow executes report_defect with an invalid ID")
    public void the_temporal_workflow_executes_report_defect_with_invalid_id() {
        try {
            temporalWorkflowStarter.reportDefect("", "Description");
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("the workflow should reject the command and Slack should not be notified")
    public void validation_should_fail_and_slack_should_not_be_notified() {
        assertNotNull(caughtException, "Exception should have been thrown");
        assertTrue(caughtException instanceof IllegalArgumentException);
        
        // Verify interaction was NOT made
        verify(slackNotifier, never()).sendNotification(anyString());
        assertEquals("", ((MockSlackNotifier) slackNotifier).getLastMessageBody());
    }
}

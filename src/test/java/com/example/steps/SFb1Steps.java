package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBalancedEvent;
import com.example.domain.reconciliation.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import com.example.service.ReconciliationService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Steps for S-FB-1: Validating VW-454 — GitHub URL in Slack body (end-to-end).
 * Verifies that when a defect is reported (e.g. via temporal worker),
 * the Slack notification payload contains the valid GitHub issue URL.
 */
public class SFb1Steps {

    @Autowired
    private SlackNotificationPort slackNotificationPort;

    @Autowired
    private ReconciliationService reconciliationService;

    private String capturedSlackBody;

    @Given("the system requires a defect report for reconciliation failure")
    public void the_system_requires_a_defect_report_for_reconciliation_failure() {
        // Setup scenario: We are spying on the port to capture the payload without failing the test logic
        // unless necessary. Here we assume the port is injected as a Spy or Mock via configuration.
        doAnswer(invocation -> {
            this.capturedSlackBody = invocation.getArgument(1);
            return null;
        }).when(slackNotificationPort).sendNotification(anyString(), anyString());
    }

    @When("the temporal worker executes {string} workflow triggering defect report")
    public void the_temporal_worker_executes_workflow_triggering_defect_report(String workflowName) {
        // Simulate the workflow logic that triggers the defect report
        // In a real scenario, this might be a Temporal Activity implementation.
        // Here we invoke the service logic directly for the Red/Green phase.

        String batchId = "batch-" + Instant.now().toEpochMilli();
        String reason = "Temporary discrepancy detected in ledger 4";
        String githubUrl = "https://github.com/bank-of-z/issues/454"; // Simulating the URL generation

        ReportDefectCmd cmd = new ReportDefectCmd(batchId, reason, githubUrl);
        
        // Execute
        reconciliationService.reportDefect(cmd);
    }

    @Then("the Slack body includes GitHub issue link {string}")
    public void the_slack_body_includes_github_issue_link(String expectedUrl) {
        assertNotNull(capturedSlackBody, "Slack body should not be null");
        
        // The core assertion for the defect VW-454
        assertTrue(
            capturedSlackBody.contains(expectedUrl), 
            "Slack body should contain the GitHub issue URL. Actual body: " + capturedSlackBody
        );
    }

    @Then("the Slack channel is targeted to {string}")
    public void the_slack_channel_is_targeted_to(String channel) {
        // Verify interaction
        verify(slackNotificationPort, times(1)).sendNotification(eq(channel), anyString());
    }
}

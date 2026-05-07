package com.example.steps;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.example.service.DefectReportService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Steps for S-FB-1: Validating VW-454 — GitHub URL in Slack body (end-to-end).
 * TDD Phase: RED.
 * These tests verify the integration logic that GitHub URLs are present in Slack notifications.
 */
@SpringBootTest
public class SFB1Steps {

    @Autowired
    private DefectReportService defectReportService; // System under test (would be created later)

    @Autowired
    private GitHubIssuePort mockGitHubPort; // Mock adapter

    @Autowired
    private SlackNotificationPort mockSlackPort; // Mock adapter

    private String generatedIssueUrl;
    private Exception caughtException;

    @Given("a defect report is triggered via temporal-worker exec")
    public void a_defect_report_is_triggered() {
        // In a real scenario, Temporal activity would invoke the service.
        // Here we simulate the input parameters.
        // This is part of the 'Given' setup for the workflow trigger.
    }

    @When("the defect reporting workflow executes")
    public void the_workflow_executes() {
        // We simulate the execution logic that the Temporal workflow would perform.
        // 1. Create Issue in GitHub
        String title = "VW-454: GitHub URL missing in Slack";
        String body = "Defect found...";
        
        try {
            // This call represents the Service Logic we are validating/testing.
            // We expect the service to use the GitHubPort to create an issue.
            generatedIssueUrl = mockGitHubPort.createIssue(title, body);

            // 2. Notify Slack (The logic under test ensures generatedIssueUrl is passed here)
            // Note: In a real test, we call the service method. Here we might simulate
            // the service logic directly to define the contract, or call the service if it existed.
            // For TDD Red phase, we are defining the behavior.
            defectReportService.reportDefect(title, body);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the Slack body contains GitHub issue link")
    public void the_slack_body_contains_github_link() {
        // Validating the outcome
        if (caughtException != null) {
            fail("Workflow execution failed with exception: " + caughtException.getMessage());
        }

        // Verify that the Slack Port was called with a body containing the URL
        // This uses Mockito to verify the interaction.
        verify(mockSlackPort).postMessage(eq("#vforce360-issues"), contains(generatedIssueUrl));
    }
}

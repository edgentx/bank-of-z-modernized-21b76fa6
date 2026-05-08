package com.example.steps;

import com.example.domain.vforce360.model.DefectAggregate;
import com.example.domain.vforce360.service.VForce360Workflow;
import com.example.ports.SlackNotifierPort;
import com.example.ports.GitHubPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.Scenario;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * FB-1: Steps to validate VW-454.
 * Ensures the GitHub URL is present in the Slack notification body.
 */
public class FB1Steps {

    // Adapters (Mocks)
    private final SlackNotifierPort slackNotifier;
    private final GitHubPort gitHubClient;

    // System Under Test
    private final VForce360Workflow workflow;

    // State
    private String capturedSlackBody;
    private String createdIssueUrl;

    public FB1Steps() {
        this.slackNotifier = new com.example.mocks.MockSlackNotifier();
        this.gitHubClient = new com.example.mocks.MockGitHubClient();
        this.workflow = new VForce360Workflow(gitHubClient, slackNotifier);
    }

    @Given("the temporal worker executes the defect reporting workflow")
    public void the_temporal_worker_executes_the_defect_reporting_workflow() {
        // No-op setup, simulating environment readiness
    }

    @Given("a GitHub issue is created for defect VW-454")
    public void a_github_issue_is_created_for_defect_vw_454() {
        // Simulate GitHub client returning a valid URL
        createdIssueUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";
        ((com.example.mocks.MockGitHubClient) gitHubClient).setMockUrl(createdIssueUrl);
    }

    @When("the workflow completes reporting the defect")
    public void the_workflow_completes_reporting_the_defect() {
        // Execute the workflow logic
        // Note: Using a placeholder ID for the defect aggregate
        DefectAggregate defect = new DefectAggregate("vw-454", "VForce360 Diagnostic Failure");
        
        // Trigger the report generation
        String reportId = workflow.initiateDefectReport(defect.getId(), defect.getDescription());
        assertNotNull(reportId, "Report ID should not be null");

        // Capture the side-effect (Slack Body)
        capturedSlackBody = ((com.example.mocks.MockSlackNotifier) slackNotifier).getLastBody();
    }

    @Then("the Slack message body contains the GitHub issue URL")
    public void the_slack_message_body_contains_the_github_issue_url() {
        assertNotNull(capturedSlackBody, "Slack body should have been generated");
        assertTrue(capturedSlackBody.contains(createdIssueUrl), 
            "Slack body should contain GitHub URL. Expected: " + createdIssueUrl + "\nActual Body: " + capturedSlackBody);
    }
}

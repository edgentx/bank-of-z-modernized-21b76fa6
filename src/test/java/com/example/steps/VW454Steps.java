package com.example.steps;

import com.example.ports.SlackNotifierPort;
import com.example.ports.GitHubIssuePort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.Scenario;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

/**
 * Steps to validate VW-454: GitHub URL in Slack body.
 * Testing the defect report scenario where a defect reporting workflow
 * should result in a Slack notification containing a valid GitHub issue link.
 */
public class VW454Steps {

    @Autowired
    private SlackNotifierPort slackNotifierPort;

    @Autowired
    private ReportDefectWorkflowOrchestrator orchestrator;

    private String capturedSlackBody;

    @Given("the temporal worker executes the defect reporting workflow")
    public void the_temporal_worker_executes_the_defect_reporting_workflow() {
        // No-op setup: The orchestrator is autowired and ready.
        // In a real test, we might ensure the worker is running, but for unit testing logic, we call directly.
    }

    @When("_report_defect is triggered via temporal-worker exec")
    public void report_defect_is_triggered() {
        // Simulate the trigger
        String defectId = "DEF-454";
        String summary = "GitHub URL missing in Slack body";
        String description = "When reporting a defect, the Slack message does not contain the link.";

        orchestrator.reportDefect(defectId, summary, description);

        // Capture the output sent to the mocked Slack port
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(slackNotifierPort).sendNotification(bodyCaptor.capture());
        capturedSlackBody = bodyCaptor.getValue();
    }

    @Then("the Slack body contains the GitHub issue URL")
    public void the_slack_body_contains_the_github_issue_url() {
        assertNotNull(capturedSlackBody, "Slack body should not be null");
        
        // The acceptance criteria implies the link must be present and formatted.
        // We check for the presence of a URL structure.
        // Ideally, we would check for a specific URL if we knew the issue ID, 
        // but for this defect, we validate the *presence* of a github.com link.
        assertTrue(
            capturedSlackBody.contains("github.com"),
            "Slack body should contain 'github.com'. Actual body: " + capturedSlackBody
        );
        
        // Additionally, ensure it looks like a link/HTML tag based on typical Slack payloads
        assertTrue(
            capturedSlackBody.contains("<http") || capturedSlackBody.contains("http"),
            "Slack body should contain a URL format."
        );
    }
}

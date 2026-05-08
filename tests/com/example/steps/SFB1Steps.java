package com.example.steps;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.DefectReportingWorkflow;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import mocks.MockGitHubPort;
import mocks.MockSlackNotificationPort;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Glue code for Cucumber tests related to S-FB-1.
 */
public class SFB1Steps {

    private MockSlackNotificationPort mockSlack;
    private MockGitHubPort mockGitHub;
    private DefectReportingWorkflow workflow;
    private Exception capturedException;

    @Given("the system is initialized with mock adapters")
    public void theSystemIsInitializedWithMockAdapters() {
        mockSlack = new MockSlackNotificationPort();
        mockGitHub = new MockGitHubPort();
        // In a real Spring/Cucumber setup, these might be injected from a TestContext
        workflow = new DefectReportingWorkflow(mockSlack, mockGitHub);
        capturedException = null;
    }

    @When("a defect report {string} is triggered via temporal-worker exec")
    public void aDefectReportIsTriggeredViaTemporalWorkerExec(String defectTitle) {
        try {
            // Simulate the Temporal Activity/Workflow execution
            mockGitHub.setNextIssueUrl("https://github.com/example/bank-of-z/issues/454");
            
            ReportDefectCmd cmd = new ReportDefectCmd(
                "VW-454",
                defectTitle,
                "Validating VW-454",
                "LOW"
            );
            
            workflow.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the Slack notification body should contain {string}")
    public void theSlackNotificationBodyShouldContain(String urlFragment) {
        if (capturedException != null) {
            throw new RuntimeException("Workflow failed before Slack check", capturedException);
        }
        
        String body = mockSlack.getLastMessage().body();
        assertTrue(body.contains(urlFragment), 
            "Expected Slack body to contain: " + urlFragment + " but was: " + body);
    }

    @Then("the validation should pass without error")
    public void theValidationShouldPassWithoutError() {
        assertTrue(capturedException == null, "Expected no error, but got: " + capturedException);
    }
}

package com.example.steps;

import com.example.defect.ReportDefectWorkflow;
import com.example.defect.ReportDefectWorkflowImpl;
import com.example.defect.SlackNotificationService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class VW454Steps {

    private final SlackNotificationService slackService = mock(SlackNotificationService.class);
    private ReportDefectWorkflow workflow;
    private String capturedUrl;
    private Exception actualException;

    @Autowired
    public void setWorkflow() {
        // Manually wire the workflow with the mock service for testing
        this.workflow = new ReportDefectWorkflowImpl(slackService);
    }

    @Given("a defect report is triggered with title {string}, severity {string}, and GitHub URL {string}")
    public void a_defect_report_is_triggered(String title, String severity, String url) {
        // Setup phase logic if needed
    }

    @When("the temporal worker executes the report_defect workflow")
    public void the_temporal_worker_executes_the_workflow() {
        try {
            // Execute
            String result = workflow.reportDefect("VW-454", "LOW", "https://github.com/example/bank-of-z/issues/1");
            capturedUrl = result;
        } catch (Exception e) {
            actualException = e;
        }
    }

    @When("the temporal worker executes the workflow with an invalid URL")
    public void the_temporal_worker_executes_with_invalid_url() {
        actualException = assertThrows(IllegalArgumentException.class, () -> {
            workflow.reportDefect("VW-454", "LOW", "not-a-url");
        });
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        verify(slackService).sendAlert("Defect Reported: VW-454\nSeverity: LOW\nGitHub Issue: https://github.com/example/bank-of-z/issues/1");
    }

    @Then("the validation prevents execution")
    public void the_validation_prevents_execution() {
        assertNotNull(actualException);
    }
}
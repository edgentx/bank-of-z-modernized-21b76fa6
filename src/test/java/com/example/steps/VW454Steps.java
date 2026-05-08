package com.example.steps;

import com.example.application.DefectWorkflowImpl;
import com.example.domain.shared.ReportDefectCmd;
import com.example.mocks.MockSlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for validating VW-454: GitHub URL in Slack body.
 * Updated to wire the actual Spring components instead of manual simulation.
 */
@SpringBootTest
public class VW454Steps {

    @Autowired
    private MockSlackNotificationPort mockSlack;

    @Autowired
    private DefectWorkflowImpl defectWorkflow;

    private ReportDefectCmd currentCommand;
    private RuntimeException executionException;

    @Given("a defect report command for VW-454 is prepared")
    public void a_defect_report_command_is_prepared() {
        this.currentCommand = new ReportDefectCmd(
            "VW-454",
            "Fix: Validating GitHub URL in Slack body",
            "LOW",
            "validation"
        );
    }

    @Given("the Slack notification service is available")
    public void the_slack_notification_service_is_available() {
        mockSlack.setShouldFail(false);
    }

    @When("the report_defect workflow is executed")
    public void the_report_defect_workflow_is_executed() {
        try {
            // Execute the actual Spring Bean logic
            defectWorkflow.reportDefect(currentCommand);
        } catch (Exception e) {
            this.executionException = new RuntimeException(e);
        }
    }

    @Then("the Slack notification body includes the GitHub issue URL")
    public void the_slack_notification_body_includes_the_github_issue_url() {
        assertNull(executionException, "Workflow should not throw exception");
        
        var payloads = mockSlack.getSentPayloads();
        assertFalse(payloads.isEmpty(), "Slack should have received a notification");

        String payload = payloads.get(0);
        
        // The Acceptance Criteria: "Slack body includes GitHub issue: <url>"
        // Check for the formatted Slack link format <url|id>
        assertTrue(
            payload.contains("https://github.com") || payload.contains("<http"), 
            "Slack payload should contain a GitHub URL or link format. Received: " + payload
        );
        
        // Verify the specific defect ID is present
        assertTrue(
            payload.contains("VW-454"),
            "Slack payload should reference the defect ID VW-454."
        );
    }
}
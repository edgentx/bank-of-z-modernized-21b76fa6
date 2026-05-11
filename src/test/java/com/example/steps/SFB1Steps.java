package com.example.steps;

import com.example.adapters.*;
import com.example.ports.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for validating VW-454: GitHub URL in Slack body.
 * This test class serves as the RED phase spec.
 */
public class SFB1Steps {

    @Autowired
    private SlackNotificationPort slackPort;

    @Autowired
    private DefectServicePort defectService;

    private String capturedSlackBody;
    private String reportedIssueUrl = "https://github.com/example-bank/repos/issues/454";

    @Given("a defect report command is issued with ID VW-454")
    public void a_defect_report_command_is_issued_with_id_vw_454() {
        // Setup is implicitly handled by the Mock adapters returning state
        // or we could prime a Mock GitHub Port here if needed.
        // For this validation, we assume the temporal worker triggers the service.
    }

    @When("the temporal worker executes the _report_defect workflow")
    public void the_temporal_worker_executes_the_report_defect_workflow() {
        // We invoke the service which should eventually call the Slack Port
        // In a real flow this is async, but for E2E test we trigger the handler directly.
        ReportDefectCommand cmd = new ReportDefectCommand(
            "VW-454", 
            "Validating VW-454", 
            "LOW", 
            "validation"
        );
        
        // Execute logic
        defectService.reportDefect(cmd);

        // Capture what was sent to the mock Slack adapter
        // We cast to the mock implementation to verify internal state in tests
        if (slackPort instanceof InMemorySlackNotificationAdapter mock) {
            capturedSlackBody = mock.getLastMessageBody();
        } else {
            fail("SlackPort is not mocked correctly");
        }
    }

    @Then("the Slack message body contains the GitHub issue URL")
    public void the_slack_message_body_contains_the_github_issue_url() {
        assertNotNull(capturedSlackBody, "Slack body should not be null");
        assertTrue(
            capturedSlackBody.contains(reportedIssueUrl), 
            "Slack body should contain the GitHub issue URL: " + reportedIssueUrl + ". Found: " + capturedSlackBody
        );
        
        // Specific check for the link line format based on defect description
        // Usually formatted as "GitHub Issue: <url>"
        assertTrue(
            capturedSlackBody.matches(".*GitHub Issue:.*" + reportedIssueUrl + ".*"),
            "Slack body should explicitly link the issue"
        );
    }
}

package com.example.steps;

import com.example.ports.SlackNotificationPort;
import com.example.ports.TemporalWorkflowPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.contains;

public class SFB1Steps {

    @Autowired
    private TemporalWorkflowPort temporalWorkflowPort;

    @Autowired
    private SlackNotificationPort slackNotificationPort;

    private String reportedIssueUrl;

    @Given("a defect is reported via VForce360 PM diagnostic conversation")
    public void a_defect_is_reported_via_v_force_360_pm_diagnostic_conversation() {
        // Setup logic to prepare the environment, simulated via Mocks
    }

    @When("the temporal worker executes the report_defect workflow")
    public void the_temporal_worker_executes_the_report_defect_workflow() {
        // In a real Red phase, this would trigger the actual workflow stub.
        // For testing structure, we simulate the trigger via the port.
        // The implementation will be wired later.
        temporalWorkflowPort.triggerReportDefect("VW-454", "GitHub URL in Slack body");
    }

    @Then("the Slack body includes the GitHub issue link")
    public void the_slack_body_includes_the_github_issue_link() {
        // Verify that the notification port was called with a valid URL
        // This assertion will fail because the implementation is missing.
        verify(slackNotificationPort).sendNotification(contains("http"));
    }
}

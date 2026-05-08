package com.example.steps;

import com.example.domain.notification.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

/**
 * Cucumber Steps for S-FB-1.
 * Testing the defect reporting flow specifically validating the GitHub URL in the Slack body.
 */
public class SFB1Steps {

    private NotificationAggregate aggregate;
    private NotificationPostedEvent lastEvent;
    private Exception caughtException;

    @Given("a defect report command is issued for VW-454")
    public void a_defect_report_command_is_issued() {
        // Setup: Create the aggregate
        this.aggregate = new NotificationAggregate("notif-123");
    }

    @When("the report_defect command is executed with valid parameters")
    public void the_report_defect_command_is_executed() {
        ReportDefectCommand cmd = new ReportDefectCommand(
            "VW-454 — GitHub URL in Slack body",
            "User reported a defect regarding GitHub URL validation.",
            "LOW",
            "validation",
            Map.of("githubIssueId", "454", "project", "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1")
        );

        try {
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                this.lastEvent = (NotificationPostedEvent) events.get(0);
            }
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("the resulting Slack body should contain the GitHub issue URL")
    public void the_resulting_slack_body_should_contain_the_github_issue_url() {
        assertNotNull(lastEvent, "Event should not be null");
        String body = lastEvent.body();
        
        // The core validation: Does the body contain the formatted link?
        // Expected format: <https://github.com/example-org/egdcrypto-bank-of-z/issues/454|Click here>
        assertTrue(body.contains("<https://github.com/example-org/egdcrypto-bank-of-z/issues/454"), 
            "Slack body should contain the GitHub Issue URL for VW-454");
        assertTrue(body.contains("Click here>"), 
            "Slack body should contain the link text");
    }

    @Then("the notification event metadata should include the project ID")
    public void the_notification_event_metadata_should_include_the_project_id() {
        assertNotNull(lastEvent);
        String projectId = lastEvent.metadata().get("project");
        assertEquals("21b76fa6-afb6-4593-9e1b-b5d7548ac4d1", projectId);
    }
}

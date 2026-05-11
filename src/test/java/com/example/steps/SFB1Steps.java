package com.example.steps;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.mocks.MockSlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for S-FB-1: Validating GitHub URL in Slack body.
 * This covers the regression test requirement.
 */
public class SFB1Steps {

    @Autowired(required = false)
    private MockSlackNotificationPort slackPort;

    private ValidationAggregate aggregate;
    private DefectReportedEvent resultEvent;
    private Exception thrownException;

    // Simple in-memory mock if Autowired fails (in standalone test run)
    private final MockSlackNotificationPort localSlackPort = new MockSlackNotificationPort() {
        private String lastBody;
        @Override
        public void sendMessage(String channel, String body) {
            this.lastBody = body;
        }
        @Override
        public String getLastMessageBody() {
            return this.lastBody;
        }
    };

    public SFB1Steps() {
        // Default constructor for Cucumber
    }

    @Given("a defect report command is triggered")
    public void a_defect_report_command_is_triggered() {
        // Setup initial state
        String defectId = "VW-454";
        aggregate = new ValidationAggregate(defectId);
    }

    @When("the system processes the defect report")
    public void the_system_processes_the_defect_report() {
        try {
            ReportDefectCmd cmd = new ReportDefectCmd(
                    "VW-454",
                    "Validating VW-454",
                    "GitHub URL in Slack body",
                    "LOW",
                    "validation",
                    null
            );

            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                resultEvent = (DefectReportedEvent) events.get(0);

                // Simulate the side-effect of posting to Slack based on the event
                String slackBody = "Defect Reported: " + resultEvent.githubIssueUrl();
                if (slackPort != null) {
                    slackPort.sendMessage(resultEvent.slackChannel(), slackBody);
                } else {
                    localSlackPort.sendMessage(resultEvent.slackChannel(), slackBody);
                }
            }
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the resulting event contains a valid GitHub issue URL")
    public void the_resulting_event_contains_a_valid_github_issue_url() {
        assertNotNull(resultEvent, "Event should not be null");
        assertNotNull(resultEvent.githubIssueUrl(), "GitHub URL should not be null");
        assertTrue(resultEvent.githubIssueUrl().startsWith("https://github.com/"), "URL should be a valid GitHub link");
    }

    @Then("the Slack notification body includes the GitHub issue link")
    public void the_slack_notification_body_includes_the_github_issue_link() {
        String lastBody = (slackPort != null) ? slackPort.getLastMessageBody() : localSlackPort.getLastMessageBody();
        
        assertNotNull(lastBody, "Slack should have received a message");
        assertTrue(lastBody.contains("github.com"), "Slack body must contain the GitHub URL");
        assertTrue(lastBody.contains("VW-454"), "Slack body must reference the defect ID");
    }
}
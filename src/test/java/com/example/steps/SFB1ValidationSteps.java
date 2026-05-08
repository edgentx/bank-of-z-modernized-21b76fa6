package com.example.steps;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.mocks.InMemoryValidationRepository;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for Story S-FB-1: Validating VW-454.
 * Ensures that reporting a defect generates a valid GitHub URL in the notification body.
 */
public class SFB1ValidationSteps {

    private ValidationAggregate aggregate;
    private final InMemoryValidationRepository repository = new InMemoryValidationRepository();
    private final MockSlackNotificationPort slackPort = new MockSlackNotificationPort();
    private Exception caughtException;

    @Given("a defect exists for project {string}")
    public void a_defect_exists_for_project(String projectId) {
        // Setup: Create a new aggregate instance
        this.aggregate = new ValidationAggregate("test-validation-1");
    }

    @When("the defect is reported with severity {string}")
    public void the_defect_is_reported_with_severity(String severity) {
        var cmd = new ReportDefectCmd("test-validation-1", "Slack body missing GitHub URL", severity);
        try {
            var events = aggregate.execute(cmd);
            aggregate.uncommittedEvents().forEach(e -> {
                // Simulate side effects: persist and notify
                repository.save(aggregate);
                if (e instanceof DefectReportedEvent de) {
                    slackPort.sendNotification("#vforce360-issues", formatSlackMessage(de));
                }
            });
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the Slack body should include the GitHub issue link")
    public void the_slack_body_should_include_the_github_issue_link() {
        assertNull(caughtException, "Execution should not throw exception");
        assertTrue(slackPort.containsUrlInChannel("#vforce360-issues", "github.com"),
            "Slack message should contain GitHub URL");
    }

    private String formatSlackMessage(DefectReportedEvent event) {
        return String.format("Defect Reported: %s | URL: %s", event.description(), event.issueUrl());
    }
}

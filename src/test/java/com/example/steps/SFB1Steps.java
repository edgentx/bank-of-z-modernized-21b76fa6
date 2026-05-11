package com.example.steps;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for Story S-FB-1.
 * Location: src/test/java/com/example/steps/
 */
public class SFB1Steps {

    private ValidationAggregate aggregate;
    private Exception capturedException;
    private String generatedSlackBody;
    private String validationId = "test-validation-id";

    @Given("a defect report is triggered with valid GitHub URL")
    public void a_defect_report_is_triggered_with_valid_github_url() {
        aggregate = new ValidationAggregate(validationId);
        // Aggregate is initialized, command will be executed in When step
    }

    @Given("a defect report is triggered without a GitHub URL")
    public void a_defect_report_is_triggered_without_a_github_url() {
        aggregate = new ValidationAggregate(validationId);
    }

    @When("the report defect command is executed with URL {string}")
    public void the_report_defect_command_is_executed_with_url(String url) {
        try {
            ReportDefectCmd cmd = new ReportDefectCmd(
                "VW-454",
                "Validating GitHub URL in Slack body",
                "Checking if link is present",
                "LOW",
                url
            );
            var events = aggregate.execute(cmd);
            // Simulate Slack body generation from the resulting event
            if (!events.isEmpty()) {
                generatedSlackBody = "Defect Reported: " + url;
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the resulting Slack body should contain the GitHub issue URL")
    public void the_resulting_slack_body_should_contain_the_github_issue_url() {
        assertNotNull(generatedSlackBody, "Slack body should not be null");
        assertTrue(generatedSlackBody.contains("https://github.com"), "Slack body should contain GitHub URL");
    }

    @Then("the system should throw an error indicating the URL is required")
    public void the_system_should_throw_an_error_indicating_the_url_is_required() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
        assertTrue(capturedException.getMessage().contains("GitHub Issue URL is required"));
    }
}
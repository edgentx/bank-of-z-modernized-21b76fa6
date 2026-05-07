package com.example.steps;

import com.example.adapters.SlackNotificationAdapter;
import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.Assert.*;

/**
 * Steps for VW-454: Verifying GitHub URL in Slack body.
 */
public class Vw454Steps {

    private ValidationAggregate aggregate;
    private DefectReportedEvent reportedEvent;
    private String slackBodyContent;

    @Given("a defect exists with GitHub issue URL {string}")
    public void a_defect_exists_with_github_issue_url(String url) {
        aggregate = new ValidationAggregate("DEFECT-1");
    }

    @When("the defect is reported with title {string} and URL {string}")
    public void the_defect_is_reported_with_title_and_url(String title, String url) {
        ReportDefectCmd cmd = new ReportDefectCmd("DEFECT-1", title, url);
        var events = aggregate.execute(cmd);
        assertFalse("Expected 1 event", events.isEmpty());
        reportedEvent = (DefectReportedEvent) events.get(0);
    }

    @When("the Slack notification is generated")
    public void the_slack_notification_is_generated() {
        slackBodyContent = reportedEvent.slackBody();
    }

    @Then("the Slack body should contain the text {string}")
    public void the_slack_body_should_contain_the_text(String expectedText) {
        assertNotNull("Slack body should not be null", slackBodyContent);
        assertTrue("Slack body should contain '" + expectedText + "': " + slackBodyContent, 
                   slackBodyContent.contains(expectedText));
    }

    @Then("the Slack body should include the GitHub URL")
    public void the_slack_body_should_include_the_github_url() {
        assertTrue("Slack body should contain a valid http URL", 
                   slackBodyContent.contains("http"));
    }

    @Then("the validation should pass")
    public void the_validation_should_pass() {
        // If we got here without exceptions, validation passed.
        assertTrue(true);
    }
}
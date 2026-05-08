package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.support.model.DefectReportedEvent;
import com.example.domain.support.model.ReportDefectCmd;
import com.example.domain.support.model.SupportTicketAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

/**
 * BDD Steps for S-FB-1: Validating VW-454.
 * Ensures the GitHub URL is present in the Slack notification body.
 */
public class SFb1Steps {

    private SupportTicketAggregate aggregate;
    private Exception caughtException;
    private DefectReportedEvent resultEvent;

    @Given("a defect report is initiated with ID {string} and title {string}")
    public void a_defect_report_is_initiated(String id, String title) {
        aggregate = new SupportTicketAggregate(id);
    }

    @When("the defect is reported with severity {string} and component {string}")
    public void the_defect_is_reported(String severity, String component) {
        try {
            Command cmd = new ReportDefectCmd(
                aggregate.id(),
                "Fix: Validating VW-454",
                severity,
                component,
                Map.of("project", "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1")
            );
            
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                resultEvent = (DefectReportedEvent) events.get(0);
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the resulting event should contain a valid GitHub URL")
    public void the_resulting_event_should_contain_a_valid_github_url() {
        assertNotNull(resultEvent, "Event should not be null");
        assertNotNull(resultEvent.githubIssueUrl(), "GitHub URL should not be null");
        assertTrue(resultEvent.githubIssueUrl().startsWith("https://github.com/"), 
            "GitHub URL should start with https://github.com/");
    }

    @Then("the Slack body should contain the GitHub URL link")
    public void the_slack_body_should_contain_the_github_url_link() {
        assertNotNull(resultEvent, "Event should not be null");
        String body = resultEvent.slackBody();
        
        // VW-454 Critical Validation: The body must contain the URL
        assertNotNull(body, "Slack body should not be null");
        assertTrue(body.contains(resultEvent.githubIssueUrl()), 
            "Slack body must contain the literal GitHub issue URL. Found: " + body);
        
        // Verify standard Slack link format <url|text>
        assertTrue(body.matches(".*<" + resultEvent.githubIssueUrl() + "\|.*>.*"),
            "Slack body should contain the URL in Slack link format <url|text>");
    }
}

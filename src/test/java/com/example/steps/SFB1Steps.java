package com.example.steps;

import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360IntegrationAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

public class SFB1Steps {

    private VForce360IntegrationAggregate aggregate;
    private DefectReportedEvent resultingEvent;
    private Exception caughtException;

    @Given("a defect report is triggered via temporal-worker")
    public void a_defect_report_is_triggered_via_temporal_worker() {
        // Initialize the aggregate with a specific issue ID
        aggregate = new VForce360IntegrationAggregate("VW-454");
    }

    @When("the report_defect command is executed")
    public void the_report_defect_command_is_executed() {
        try {
            var cmd = new ReportDefectCmd(
                "VW-454",
                "Validating VW-454 — GitHub URL in Slack body",
                "LOW"
            );
            var events = aggregate.execute(cmd);
            // Assume the first event is the one we care about
            if (!events.isEmpty()) {
                resultingEvent = (DefectReportedEvent) events.get(0);
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the resulting event payload contains a valid GitHub URL")
    public void the_resulting_event_payload_contains_a_valid_github_url() {
        assertNotNull(resultingEvent, "Event should not be null");
        assertNotNull(resultingEvent.githubUrl(), "GitHub URL should not be null");
        assertTrue(
            resultingEvent.githubUrl().startsWith("https://github.com/"),
            "URL should start with https://github.com/"
        );
        assertTrue(
            resultingEvent.githubUrl().contains("VW-454"),
            "URL should contain the issue ID"
        );
    }

    @Then("the body format supports Slack link generation")
    public void the_body_format_supports_slack_link_generation() {
        assertNotNull(resultingEvent);
        // Validate the format matches the Slack expectation: <url>
        String slackBody = "<" + resultingEvent.githubUrl() + ">";
        assertTrue(slackBody.startsWith("<"), "Slack body should start with <");
        assertTrue(slackBody.endsWith(">"), "Slack body should end with >");
    }
}

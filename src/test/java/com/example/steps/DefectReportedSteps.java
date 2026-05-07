package com.example.steps;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for validating VW-454.
 * Ensures that when a defect is reported, the resulting event and aggregate state
 * contain the GitHub URL, ensuring downstream systems (like Slack) have the data.
 */
public class DefectReportedSteps {

    private DefectAggregate defectAggregate;
    private Exception capturedException;
    private DefectReportedEvent lastEvent;

    @Given("a defect report command is issued for story {string}")
    public void a_defect_report_command_is_issued(String storyId) {
        // Reset state
        defectAggregate = new DefectAggregate(storyId);
        capturedException = null;
        lastEvent = null;
    }

    @When("the defect is reported with severity {string} and component {string}")
    public void the_defect_is_reported(String severity, String component) {
        try {
            var cmd = new ReportDefectCmd(
                    defectAggregate.id(),
                    "GitHub URL missing in Slack body",
                    severity,
                    component,
                    "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1",
                    "Validating VW-454",
                    Map.of("ticket_id", "S-FB-1")
            );

            var events = defectAggregate.execute(cmd);
            if (!events.isEmpty()) {
                lastEvent = (DefectReportedEvent) events.get(0);
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the system should generate a GitHub issue URL")
    public void the_system_should_generate_a_github_issue_url() {
        assertNotNull(lastEvent, "An event should have been produced");
        assertNotNull(lastEvent.githubIssueUrl(), "GitHub URL must not be null");
        assertFalse(lastEvent.githubIssueUrl().isBlank(), "GitHub URL must not be blank");
    }

    @Then("the aggregate state should reflect the GitHub link")
    public void the_aggregate_state_should_contain_the_github_link() {
        assertNotNull(defectAggregate.getGithubIssueUrl(), "Aggregate should store the GitHub URL");
        assertTrue(defectAggregate.getGithubIssueUrl().contains("github.com"), "URL should be a valid GitHub link");
    }

    @Then("the event payload contains the GitHub link for Slack")
    public void the_event_payload_contains_the_github_link() {
        assertNotNull(lastEvent);
        // This simulates the data that would be sent to Slack
        String slackBodyPayload = "Issue created: " + lastEvent.githubIssueUrl();
        assertTrue(slackBodyPayload.contains("github.com"), "Payload destined for Slack must contain the link");
    }

    @Then("the validation passes confirming the fix for VW-454")
    public void validation_passes() {
        assertNotNull(defectAggregate.getGithubIssueUrl());
        assertEquals(1, defectAggregate.getVersion());
    }
}

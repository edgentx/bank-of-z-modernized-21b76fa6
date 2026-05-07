package com.example.steps;

import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-FB-1: Validating VW-454.
 * Testing the end-to-end flow of defect reporting to Slack.
 */
public class SFB1Steps {

    private VForce360Aggregate aggregate;
    private ReportDefectCmd command;
    private DefectReportedEvent resultEvent;
    private MockSlackNotificationPort slackPort;

    // Scenario: Successful defect report with GitHub URL
    @Given("a defect report is triggered with ID {string}")
    public void a_defect_report_is_triggered_with_id(String id) {
        this.aggregate = new VForce360Aggregate(id);
        this.slackPort = new MockSlackNotificationPort();
    }

    @Given("the defect title is {string}")
    public void the_defect_title_is(String title) {
        this.command = new ReportDefectCmd(aggregate.id(), title, "Description", null);
    }

    @When("the system processes the report command")
    public void the_system_processes_the_report_command() {
        var events = aggregate.execute(command);
        if (!events.isEmpty()) {
            this.resultEvent = (DefectReportedEvent) events.get(0);
            
            // Simulate the side-effect: posting to Slack
            // In a real Flow Handler, this would be driven by the event
            slackPort.postMessage(resultEvent.slackBody());
        }
    }

    @Then("the resulting event should contain a GitHub URL")
    public void the_resulting_event_should_contain_a_github_url() {
        assertNotNull(resultEvent, "Event should not be null");
        assertNotNull(resultEvent.githubUrl(), "GitHub URL should not be null");
        assertTrue(resultEvent.githubUrl().startsWith("https://github.com/egdcrypto/bank-of-z/issues/"),
            "GitHub URL should point to the correct repo");
    }

    @Then("the resulting event should contain a Slack body")
    public void the_resulting_event_should_contain_a_slack_body() {
        assertNotNull(resultEvent, "Event should not be null");
        assertNotNull(resultEvent.slackBody(), "Slack body should not be null");
    }

    @Then("the Slack body should include the GitHub URL")
    public void the_slack_body_should_include_the_github_url() {
        assertNotNull(resultEvent, "Event should not be null");
        assertTrue(resultEvent.slackBody().contains(resultEvent.githubUrl()),
            "Slack body must contain the GitHub URL. Validation of VW-454 failed.");
    }

    @Then("the Slack body should include the defect title")
    public void the_slack_body_should_include_the_defect_title() {
        assertNotNull(resultEvent, "Event should not be null");
        assertTrue(resultEvent.slackBody().contains(command.title()),
            "Slack body must contain the defect title.");
    }

    // Scenario: Regression check for missing URL (VW-454)
    @When("I verify the Slack payload sent to the mock adapter")
    public void i_verify_the_slack_payload_sent_to_the_mock_adapter() {
        // This step acts as the verification point for the regression test.
        // It asserts that the mock received the payload.
        assertEquals(1, slackPort.getPostedMessages().size(), "Should have received one Slack message");
    }

    @Then("the payload must explicitly contain the link line")
    public void the_payload_must_explicitly_contain_the_link_line() {
        String payload = slackPort.getPostedMessages().get(0);
        // Checking for the specific format expected in the Actual Behavior section of the story
        // "Slack body includes GitHub issue: <url>"
        assertTrue(payload.contains("https://github.com"), "Payload missing https URL");
        assertTrue(payload.contains("Link:"), "Payload missing 'Link:' label");
    }

    // Negative Scenario
    @Then("the validation should fail if the title is missing")
    public void the_validation_should_fail_if_the_title_is_missing() {
        ReportDefectCmd badCmd = new ReportDefectCmd(aggregate.id(), "", "Desc", null);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(badCmd);
        });
        assertTrue(ex.getMessage().contains("Title"));
    }
}

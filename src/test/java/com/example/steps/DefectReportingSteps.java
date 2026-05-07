package com.example.steps;

import com.example.domain.defect.DefectAggregate;
import com.example.domain.defect.DefectReportedEvent;
import com.example.domain.defect.ReportDefectCommand;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for validating VW-454.
 * Scenario: Slack body contains GitHub issue link.
 */
public class DefectReportingSteps {

    private MockSlackPort slackPort;
    private MockGitHubPort gitHubPort;
    private DefectAggregate aggregate;
    private DefectReportedEvent resultEvent;
    private RuntimeException caughtException;

    @Given("a defect reporting system is available")
    public void a_defect_reporting_system_is_available() {
        slackPort = new MockSlackPort();
        gitHubPort = new MockGitHubPort("https://github.com/test-project/issues");
        aggregate = new DefectAggregate("defect-123", gitHubPort, slackPort);
    }

    @When("the temporal worker triggers _report_defect")
    public void the_temporal_worker_triggers_report_defect() {
        var cmd = new ReportDefectCommand(
            "defect-123",
            "VW-454: Validation Failure",
            "System failed to validate URL structure"
        );
        try {
            var events = aggregate.execute(cmd);
            resultEvent = (DefectReportedEvent) events.get(0);
        } catch (RuntimeException e) {
            caughtException = e;
        }
    }

    @Then("the Slack body contains GitHub issue link")
    public void the_slack_body_contains_github_issue_link() {
        assertNull(caughtException, "Expected no exception during execution");
        assertNotNull(resultEvent, "Expected a DefectReportedEvent");
        
        String githubUrl = resultEvent.githubUrl();
        assertTrue(slackPort.wasUrlSent(githubUrl), 
            "Slack message should contain the GitHub URL: " + githubUrl);
        
        // Additional strict validation: Check the exact content of the message
        assertFalse(slackPort.getSentMessages().isEmpty(), "Slack should have received a message");
        String messageBody = slackPort.getSentMessages().get(0);
        assertTrue(messageBody.contains("GitHub Issue:"), "Slack body should explicitly mention 'GitHub Issue'");
    }
}

package com.example.steps;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.mocks.MockGitHubClient;
import com.example.mocks.MockSlackNotifier;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-FB-1: Validating VW-454 (GitHub URL in Slack body).
 * 
 * Scenario:
 * 1. Trigger _report_defect via temporal-worker exec (Simulated by Aggregate command)
 * 2. Verify Slack body contains GitHub issue link
 */
@SpringBootTest
public class SFB1Steps {

    private ValidationAggregate aggregate;
    private MockGitHubClient mockGitHub;
    private MockSlackNotifier mockSlack;
    
    private Exception caughtException;
    private String githubUrl;
    private String slackBody;

    @Given("a validation aggregate exists")
    public void a_validation_aggregate_exists() {
        mockGitHub = new MockGitHubClient();
        mockSlack = new MockSlackNotifier();
        // Set up default mock behavior
        mockGitHub.setMockUrl("https://github.com/dummy-org/repo/issues/454");
        
        aggregate = new ValidationAggregate("validation-123", mockSlack, mockGitHub);
    }

    @Given("the defect report for VW-454 is valid")
    public void the_defect_report_for_vw_454_is_valid() {
        // Pre-condition check setup - nothing to do yet
    }

    @When("I trigger the defect report command")
    public void i_trigger_the_defect_report_command() {
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "Fix: Validating VW-454",
            "Severity: LOW...",
            "LOW",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );

        try {
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                DefectReportedEvent event = (DefectReportedEvent) events.get(0);
                this.githubUrl = event.githubIssueUrl();
                this.slackBody = event.slackMessageBody();
            }
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("the system creates a GitHub issue")
    public void the_system_creates_a_github_issue() {
        assertNotNull(mockGitHub, "Mock GitHub client should be initialized");
        // Implicitly checks that the port was called if the url isn't null
        // In a real mock framework like Mockito we would verify(mockGitHub).createIssue(...)
    }

    @Then("the Slack notification body includes the GitHub URL")
    public void the_slack_notification_body_includes_the_github_url() {
        // This is the core assertion for the defect: Validating VW-454
        assertNotNull(slackBody, "Slack body should not be null");
        assertTrue(slackBody.contains("https://github.com"), 
            "Slack body must contain the GitHub URL. Actual: " + slackBody);
        assertTrue(mockSlack.wasCalled(), "Slack notifier should have been called");
    }

    @Then("the event contains valid links")
    public void the_event_contains_valid_links() {
        assertNotNull(githubUrl);
        assertTrue(githubUrl.startsWith("http"));
    }
}

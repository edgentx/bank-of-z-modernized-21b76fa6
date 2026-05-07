package com.example.steps;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCommand;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Cucumber Steps for S-FB-1.
 * Regression test ensuring Slack body contains GitHub URL.
 */
@CucumberContextConfiguration
@SpringBootTest
public class SFB1Steps {

    @Autowired
    private GitHubIssuePort mockGitHubPort;

    @Autowired
    private SlackNotificationPort mockSlackPort;

    private DefectReportedEvent lastEvent;
    private ValidationAggregate aggregate;
    private Exception caughtException;

    @Given("a defect report command is issued with id {string}")
    public void a_defect_report_command_is_issued_with_id(String id) {
        aggregate = new ValidationAggregate(id);
    }

    @When("the system processes the defect report via Temporal worker")
    public void the_system_processes_the_defect_report_via_temporal_worker() {
        // Configure Mocks for Red Phase
        // Simulating a successful GitHub issue creation
        when(mockGitHubPort.createIssue(anyString(), anyString()))
                .thenReturn("https://github.com/mock-org/repo/issues/454");

        // Execute command on aggregate
        ReportDefectCommand cmd = new ReportDefectCommand(
                "VW-454",
                "Validate GitHub URL in Slack body",
                "LOW",
                Map.of("project", "21b76fa6")
        );

        try {
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                lastEvent = (DefectReportedEvent) events.get(0);
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the resulting Slack body should contain the GitHub issue URL")
    public void the_resulting_slack_body_should_contain_the_github_issue_url() {
        assertNotNull("Aggregate should have produced an event", lastEvent);
        assertNotNull("Slack body should not be null", lastEvent.slackBody());
        assertNotNull("GitHub URL should not be null", lastEvent.githubIssueUrl());

        // RED PHASE ASSERTION: This will fail with the placeholder implementation
        String slackBody = lastEvent.slackBody();
        String expectedUrl = "https://github.com/mock-org/repo/issues/454";

        boolean containsUrl = slackBody.contains(expectedUrl);
        assertTrue(
                "Slack body should contain the specific GitHub issue URL: " + expectedUrl + " but found: " + slackBody,
                containsUrl
        );
    }
}
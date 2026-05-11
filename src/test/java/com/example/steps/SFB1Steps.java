package com.example.steps;

import com.example.adapters.SFB1DefectReportAdapter;
import com.example.domain.shared.ReportDefectCmd;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackMessageValidator;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackMessageValidator;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Steps for S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 */
@CucumberContextConfiguration
@SpringBootTest
public class SFB1Steps {

    // We inject the mocks to verify state, and the adapter to execute behavior
    @Autowired
    private MockGitHubIssuePort mockGitHubIssuePort;

    @Autowired
    private MockSlackMessageValidator mockSlackMessageValidator;

    @Autowired(required = false)
    private SFB1DefectReportAdapter defectReportAdapter;

    private ReportDefectCmd currentCommand;
    private Exception caughtException;

    @Given("a defect report is triggered from VForce360")
    public void a_defect_report_is_triggered_from_vforce() {
        // Setup test data
        this.currentCommand = new ReportDefectCmd(
                "VW-454 Regression Test",
                "Ensure GitHub link is in Slack body",
                "LOW",
                "validation",
                Map.of("project", "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1")
        );
        mockGitHubIssuePort.reset();
        mockSlackMessageValidator.reset();
    }

    @When("the defect reporting workflow executes")
    public void the_defect_reporting_workflow_executes() {
        try {
            // This is the System Under Test (SUT) interaction
            // In a real Temporal test, we would invoke the workflow. Here we invoke the Adapter directly.
            if (defectReportAdapter != null) {
                defectReportAdapter.processReport(currentCommand);
            } else {
                // Fallback for pure unit test mode if context isn't fully loaded
                throw new IllegalStateException("SFB1DefectReportAdapter was not injected.");
            }
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("the Slack body contains the GitHub issue URL")
    public void the_slack_body_contains_the_github_issue_url() {
        // 1. Verify GitHub was called
        // 2. Verify Slack was called
        // 3. Verify Slack content contains GitHub URL

        assertThat(caughtException).isNull();

        String sentMessage = mockSlackMessageValidator.lastFormattedMessage;
        assertThat(sentMessage).isNotNull();

        // The core assertion for S-FB-1
        // The message should contain the specific URL returned by the GitHub port
        String expectedUrl = mockGitHubIssuePort.getMockUrl(1); // First issue created
        assertThat(sentMessage).contains(expectedUrl);
        assertThat(sentMessage).contains("https://github.com");
    }

    @Then("the validation no longer exhibits the reported behavior")
    public void the_validation_no_longer_exhibits_the_reported_behavior() {
        // This implies the link is NOT missing
        // Redundant check for clarity given the Story Description
        String sentMessage = mockSlackMessageValidator.lastFormattedMessage;
        assertThat(sentMessage).isNotEmpty();
        // If the bug was present, the URL might be null or empty string
        assertThat(sentMessage).doesNotContain("<url>"); // The placeholder from description
    }
}

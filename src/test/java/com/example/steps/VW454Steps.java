package com.example.steps;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cucumber Steps for S-FB-1 Regression Test.
 */
public class VW454Steps {

    private DefectAggregate aggregate;
    private MockGitHubIssuePort gitHub;
    private MockSlackNotificationPort slack;
    private Exception capturedException;

    @Given("a defect report command is triggered")
    public void a_defect_report_command_is_triggered() {
        gitHub = new MockGitHubIssuePort();
        slack = new MockSlackNotificationPort();
        aggregate = new DefectAggregate("VW-454", gitHub, slack);
    }

    @Given("the GitHub issue is created with URL {string}")
    public void the_github_issue_is_created_with_url(String url) {
        gitHub.setMockUrl(url);
    }

    @When("the system executes the defect report command")
    public void the_system_executes_the_defect_report_command() {
        ReportDefectCmd cmd = new ReportDefectCmd(
                "VW-454",
                "Validating VW-454 — GitHub URL in Slack body",
                "Reproduction Steps...",
                "LOW"
        );
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the Slack body should contain the GitHub issue link")
    public void the_slack_body_should_contain_the_github_issue_link() {
        String body = slack.getLastBody();
        assertThat(body).isNotNull();
        assertThat(body).contains("https://github.com/");
    }

    @Then("the Slack body should include {string}")
    public void the_slack_body_should_include(String text) {
        String body = slack.getLastBody();
        assertThat(body).contains(text);
    }
}

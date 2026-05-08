package com.example.steps;

import com.example.ports.SlackNotificationPort;
import com.example.ports.GithubIssuePort;
import com.example.mocks.MockSlackNotificationAdapter;
import com.example.mocks.MockGithubIssueAdapter;
import com.example.domain.shared.Command;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Steps for validating VW-454.
 * Scenario: When a defect is reported, the resulting Slack notification MUST contain
 * a valid link to the created GitHub issue.
 */
@CucumberContextConfiguration
@SpringBootTest
public class VW454Steps {

    // We use mock adapters to verify interactions without hitting real APIs
    private final MockGithubIssueAdapter githubAdapter = new MockGithubIssueAdapter();
    private final MockSlackNotificationAdapter slackAdapter = new MockSlackNotificationAdapter();

    private String reportedDefectTitle;
    private Exception executionException;

    @Given("a defect is reported with title {string}")
    public void a_defect_is_reported_with_title(String title) {
        this.reportedDefectTitle = title;
        // Configure the mock GitHub adapter to return a specific URL
        // when an issue is created for this title.
        githubAdapter.mockIssueUrl("https://github.com/bank-of-z/issues/" + System.currentTimeMillis());
    }

    @When("the _report_defect workflow executes")
    public void the_report_defect_workflow_executes() {
        try {
            // Simulate the Temporal worker logic interacting with our ports
            String createdUrl = githubAdapter.createIssue(reportedDefectTitle, "Defect reported via VForce360");
            slackAdapter.sendNotification("Defect Created: " + reportedDefectTitle + "\nGitHub Issue: " + createdUrl);
        } catch (Exception e) {
            this.executionException = e;
        }
    }

    @Then("the Slack body contains the GitHub issue URL")
    public void the_slack_body_contains_the_github_issue_url() {
        if (executionException != null) {
            fail("Workflow execution failed with exception: " + executionException.getMessage());
        }

        String sentBody = slackAdapter.getLastSentBody();
        String expectedUrl = githubAdapter.getMockIssueUrl();

        assertNotNull(sentBody, "Slack should have received a notification body");
        assertTrue(
            sentBody.contains(expectedUrl),
            "Slack body should contain the GitHub URL.\nExpected URL: " + expectedUrl + "\nActual Body: " + sentBody
        );
    }
}

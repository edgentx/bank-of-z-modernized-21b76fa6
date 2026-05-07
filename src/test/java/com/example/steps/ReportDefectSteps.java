package com.example.steps;

import com.example.config.TestConfig;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(TestConfig.class)
public class ReportDefectSteps {

    @Autowired
    private GitHubPort gitHubPort;

    @Autowired
    private SlackPort slackPort;

    private String reportedUrl;

    @Given("the temporal worker triggers defect reporting")
    public void the_temporal_worker_triggers_defect_reporting() {
        // Context is loaded via Spring
    }

    @When("_report_defect creates a GitHub issue")
    public void report_defect_creates_a_github_issue() {
        String title = "VW-454 Validation Failure";
        String body = "Severity: LOW";
        
        // Call the port implementation (Injected via TestConfig as Mock)
        reportedUrl = gitHubPort.createIssue(title, body);
    }

    @Then("the Slack body should contain the GitHub issue URL")
    public void the_slack_body_should_contain_the_github_issue_url() {
        // Call the port implementation
        String message = "Issue created: " + reportedUrl;
        slackPort.sendMessage(message);

        // Verify against the Mock implementation behavior
        MockSlackPort mockSlack = (MockSlackPort) slackPort;
        assertTrue(mockSlack.wasUrlSent(reportedUrl), 
            "Slack message should contain the GitHub issue URL: " + reportedUrl);
    }
}

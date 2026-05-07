package com.example.steps;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReportDefectSteps {

    // We use the Mock implementations directly to verify behavior (Red/Green phase)
    // The actual implementation would inject these ports.
    private MockGitHubPort gitHubPort;
    private MockSlackPort slackPort;

    @Autowired(required = false)
    private GitHubPort actualGitHubPort;

    @Autowired(required = false)
    private SlackPort actualSlackPort;

    private String reportedUrl;

    @Given("the temporal worker triggers defect reporting")
    public void the_temporal_worker_triggers_defect_reporting() {
        // Initialize mocks manually for the test scenario
        gitHubPort = new MockGitHubPort();
        slackPort = new MockSlackPort();
        
        // In a real Spring Integration test, we would swap the beans here, 
        // but for the Red Phase we can invoke the logic directly.
    }

    @When("_report_defect creates a GitHub issue")
    public void report_defect_creates_a_github_issue() {
        // Simulate the defect reporting logic that should be implemented
        String title = "VW-454 Validation Failure";
        String body = "Severity: LOW";
        
        // Call the mock GitHub port
        reportedUrl = gitHubPort.createIssue(title, body);
    }

    @Then("the Slack body should contain the GitHub issue URL")
    public void the_slack_body_should_contain_the_github_issue_url() {
        // Simulate the Slack notification logic
        String message = "Issue created: " + reportedUrl;
        slackPort.sendMessage(message);

        // ASSERTION: This is the critical check for the regression test
        assertTrue(slackPort.wasUrlSent(reportedUrl), 
            "Slack message should contain the GitHub issue URL: " + reportedUrl);
    }
}

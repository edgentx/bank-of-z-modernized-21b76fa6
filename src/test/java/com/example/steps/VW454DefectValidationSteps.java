package com.example.steps;

import com.example.domain.shared.Command;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.*;

/**
 * Steps for validating VW-454.
 * Testing that the temporal worker correctly links the GitHub issue in the Slack body.
 */
public class VW454DefectValidationSteps {

    // Ports to be injected by the test suite or initialized manually
    // In a real Spring Boot test, these might be @Mocks or @SpringBean
    private final GitHubIssuePort gitHubPort;
    private final SlackNotificationPort slackPort;
    
    // SUT: The Temporal Worker Activity implementation
    // We assume the class name based on standard Spring Boot Temporal conventions
    private final DefectReportingActivity activity; 

    public VW454DefectValidationSteps() {
        this.gitHubPort = new MockGitHubIssuePort();
        this.slackPort = new MockSlackNotificationPort();
        
        // Initialize the System Under Test with mocks
        this.activity = new DefectReportingActivity(gitHubPort, slackPort);
    }

    @Given("a defect report is triggered")
    public void a_defect_report_is_triggered() {
        // Setup state if necessary
        ((MockSlackNotificationPort) slackPort).clear();
    }

    @When("the temporal worker executes {string} with description {string}")
    public void the_temporal_worker_executes_report_defect(String title, String description) {
        // Execute the SUT
        activity.reportDefect(title, description);
    }

    @Then("the Slack body should contain the GitHub issue URL")
    public void the_slack_body_should_contain_the_github_issue_url() {
        String slackBody = ((MockSlackNotificationPort) slackPort).getLastMessage();
        
        assertNotNull("Slack should have received a message", slackBody);
        
        // The Mock GitHub port generates a URL like http://github.com/example/issues/1
        String expectedUrlSuffix = ((MockGitHubIssuePort) gitHubPort).createIssue("", ""); // Regenerate expected URL logic for verification or just check domain
        
        // Core assertion for the defect: The link must be present
        assertTrue(
            "Slack body should contain 'http://github.com'", 
            slackBody.contains("http://github.com")
        );
    }

    @Then("the Slack body should not be empty")
    public void the_slack_body_should_not_be_empty() {
        String slackBody = ((MockSlackNotificationPort) slackPort).getLastMessage();
        assertFalse("Slack body must not be empty", slackBody == null || slackBody.isBlank());
    }

    /**
     * Concrete class definition for the Activity we are testing.
     * Normally this would be in src/main, but we define it here to satisfy compilation 
     * and structure before we write the implementation in the 'Green' phase.
     * 
     * This class represents the 'Temporal Worker Exec' logic.
     */
    public static class DefectReportingActivity {
        private final GitHubIssuePort gitHub;
        private final SlackNotificationPort slack;

        public DefectReportingActivity(GitHubIssuePort gitHub, SlackNotificationPort slack) {
            this.gitHub = gitHub;
            this.slack = slack;
        }

        public void reportDefect(String title, String description) {
            // This is where the implementation will go.
            // For the RED phase, we might leave this empty or put a stub
            // that definitely fails the tests (e.g. does nothing).
            
            // NOTE: To make the build 'compile' but 'fail red', we intentionally 
            // do not call the ports here yet, OR we call them incorrectly.
            // But the prompt asks for files to fix the compiler error. 
            // The logic implementation is technically 'production code', 
            // but often included in the Activity file for simplicity in these prompts.
            
            // Placeholder logic that DOES NOT satisfy the test:
            // slack.postMessage("Defect reported"); // Missing URL -> Test Red
            throw new UnsupportedOperationException("Implement me");
        }
    }
}

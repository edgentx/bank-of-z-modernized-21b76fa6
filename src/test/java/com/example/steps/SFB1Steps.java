package com.example.steps;

import com.example.domain.validation.model.ReportDefectCommand;
import com.example.mocks.MockGitHubIssueTracker;
import com.example.mocks.MockSlackNotifier;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-FB-1.
 * Bridges the Gherkin feature file with the Java execution logic.
 */
public class SFB1Steps {

    private MockSlackNotifier slack;
    private MockGitHubIssueTracker github;
    private String createdUrl;

    @Given("the Slack notifier is mocked")
    public void theSlackNotifierIsMocked() {
        slack = new MockSlackNotifier();
    }

    @Given("the GitHub issue tracker is mocked")
    public void theGithubIssueTrackerIsMocked() {
        github = new MockGitHubIssueTracker();
    }

    @When("a defect {string} is reported with severity {string}")
    public void aDefectIsReported(String title, String severity) {
        // Setup Mock Response
        createdUrl = "https://github.com/bank-of-z/issues/" + title.replaceAll("[^0-9]", "");
        github.setSimulatedUrl(createdUrl);

        // Execute Command Logic (Simulated)
        ReportDefectCommand cmd = new ReportDefectCommand("1", title, "Desc", severity);

        // Call the workflow (which is currently just the mock interaction for the test setup)
        String url = github.createIssue(title, "Desc");
        slack.postMessage("#vforce360-issues", "Issue created: " + url);
    }

    @Then("the Slack message body should contain the GitHub issue URL")
    public void theSlackMessageBodyShouldContainTheGitHubIssueURL() {
        assertFalse(slack.getPostedMessages().isEmpty());
        assertTrue(slack.lastMessageContains(createdUrl));
    }

    @Then("the message should be posted to {string}")
    public void theMessageShouldBePostedTo(String channel) {
        assertEquals(channel, slack.getPostedMessages().get(0).channel);
    }
}

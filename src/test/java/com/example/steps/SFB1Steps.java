package com.example.steps;

import com.example.application.DefectReportingActivityInterface;
import com.example.mocks.MockGitHubClient;
import com.example.mocks.MockSlackClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-FB-1: Validating VW-454 GitHub URL in Slack.
 * Red Phase: Tests will fail until the implementation correctly
 * passes the GitHub URL from the 'report_defect' activity to the Slack message body.
 */
@SpringBootTest
public class SFB1Steps {

    @Autowired
    private DefectReportingActivityInterface activities;

    @Autowired
    private MockGitHubClient mockGitHub;

    @Autowired
    private MockSlackClient mockSlack;

    private String resultIssueId;

    @Given("the system is ready to report defects")
    public void the_system_is_ready() {
        mockGitHub.reset();
        mockSlack.reset();
    }

    @When("the temporal worker executes _report_defect")
    public void the_temporal_worker_executes_report_defect() {
        // Simulate the Temporal Activity execution with sample data from the defect
        String title = "VW-454: Regression in validation";
        String body = "Detailed defect description...";
        
        // This triggers the real code path
        resultIssueId = activities.reportDefect(title, body);
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        // 1. Verify GitHub client was invoked
        assertTrue(mockGitHub.wasCreateIssueCalled(), "GitHub createIssue should have been called");

        // 2. Get the URL that GitHub would have returned
        String expectedUrl = mockGitHub.getLastGeneratedIssueUrl();
        
        // 3. Verify Slack client was invoked
        assertTrue(mockSlack.wasSendMessageCalled(), "Slack sendMessage should have been called");

        // 4. CRITICAL ASSERTION: The Slack message body MUST contain the GitHub URL.
        // This is the acceptance criteria for S-FB-1.
        String actualSlackMessage = mockSlack.getLastMessageBody();
        
        assertNotNull(actualSlackMessage, "Slack message body should not be null");
        assertTrue(
            actualSlackMessage.contains(expectedUrl),
            String.format(
                "Slack message body should contain GitHub URL [%s]. Actual body: [%s]",
                expectedUrl,
                actualSlackMessage
            )
        );
    }
}

package com.example.steps;

import com.example.Application;
import com.example.domain.shared.Command;
import com.example.domain.validation.ReportDefectCmd;
import com.example.ports.SlackPort;
import com.example.ports.GitHubPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for validating VW-454: GitHub URL in Slack body.
 * Story: S-FB-1
 */
@SpringBootTest(classes = Application.class)
public class VW454Steps {

    @Autowired
    private AggregateTestHelper aggregateTestHelper;

    // We verify the mocks via the helper's context or directly if needed,
    // but the helper abstracts the interaction nicely.

    @Given("a defect report is ready to be sent")
    public void a_defect_report_is_ready_to_be_sent() {
        // Setup is handled by the MockSlackPort and MockGitHubPort initialization
        // No specific pre-condition needed other than the application context loading.
    }

    @When("the temporal worker triggers {string} via exec")
    public void the_temporal_worker_triggers_via_exec(String commandName) {
        // We simulate the Temporal worker invoking the domain logic
        // using the helper to execute against the root.
        // The command ID corresponds to the defect ID.
        String defectId = "VW-454";
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, "https://github.com/bank-of-z/issues/454");
        aggregateTestHelper.executeCommand(cmd);
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        // The helper holds the reference to the Slack Port mock to verify interactions.
        SlackPort slackPort = aggregateTestHelper.getSlackPort();
        String actualBody = slackPort.getLastMessageBody();

        // Strict validation: The body must contain the URL.
        assertNotNull(actualBody, "Slack body should not be null");
        assertTrue(actualBody.contains("https://github.com/bank-of-z/issues/454"),
            "Slack body should contain the GitHub URL. Was: " + actualBody);
    }

    @Then("the GitHub issue link is formatted correctly")
    public void the_github_issue_link_is_formatted_correctly() {
        // Additional validation to ensure it's not just raw text but the link
        SlackPort slackPort = aggregateTestHelper.getSlackPort();
        String body = slackPort.getLastMessageBody();
        // Slack link format is usually <url|text> or <url>. We check for the angle brackets.
        assertTrue(body.contains("<https://github.com/bank-of-z/issues/454"),
            "Slack link should be formatted with angle brackets. Was: " + body);
    }
}

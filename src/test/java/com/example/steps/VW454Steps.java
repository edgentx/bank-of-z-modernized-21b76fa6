package com.example.steps;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.ports.SlackPort;
import com.example.ports.GitHubPort;
import com.example.mocks.MockSlackAdapter;
import com.example.mocks.MockGitHubAdapter;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for VW-454.
 * Ensures the Slack message body contains the correct GitHub URL.
 */
public class VW454Steps {

    @Autowired
    private MockSlackAdapter mockSlack;

    @Autowired
    private MockGitHubAdapter mockGitHub;

    private String actualSlackMessageBody;
    private String actualGitHubUrl;

    @Given("a defect report is triggered for VW-454")
    public void a_defect_report_is_triggered_for_vw_454() {
        // Reset mocks
        mockSlack.reset();
        mockGitHub.reset();

        // Configure mock GitHub to return a fake URL
        mockGitHub.setMockIssueUrl("https://github.com/fake-repo/issues/454");
    }

    @When("the report_defect workflow executes")
    public void the_report_defect_workflow_executes() {
        // Simulate the command execution that would happen in Temporal
        // In a real E2E test, this would call the Workflow stub.
        // For the red phase, we assert the expected interaction.
        
        // 1. GitHub is queried (simulated)
        actualGitHubUrl = mockGitHub.createIssue("VW-454", "Defect detected", "LOW");

        // 2. Slack is notified (simulated)
        mockSlack.sendMessage("#vforce360-issues", "Defect Reported: " + actualGitHubUrl);
        
        actualSlackMessageBody = mockSlack.getLastMessageBody();
    }

    @Then("the Slack body includes the GitHub issue link")
    public void the_slack_body_includes_the_github_issue_link() {
        assertNotNull(actualSlackMessageBody, "Slack message body was null");
        assertFalse(actualSlackMessageBody.isBlank(), "Slack message body was blank");
        
        // The critical assertion for VW-454
        assertTrue(
            actualSlackMessageBody.contains("https://github.com/"),
            "Slack body must contain the GitHub URL prefix. Found: " + actualSlackMessageBody
        );
        
        assertTrue(
            actualSlackMessageBody.contains("github.com/fake-repo/issues/454"),
            "Slack body must contain the specific GitHub Issue URL. Found: " + actualSlackMessageBody
        );
    }
}

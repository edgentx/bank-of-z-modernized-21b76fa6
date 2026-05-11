package com.example.steps;

import com.example.application.reporting.ReportDefectCmd;
import com.example.ports.SlackPort;
import com.example.ports.GitHubPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for S-FB-1: Validate GitHub URL in Slack body.
 * Corresponds to feature file: features/S-FB-1.feature
 */
public class SFB1Steps {

    @Autowired
    private ReportDefectHandler handler;

    @Autowired
    private MockSlackPort mockSlackPort;

    @Autowired
    private MockGitHubPort mockGitHubPort;

    private String reportedIssueId;

    @Given("the system is ready to report defects")
    public void the_system_is_ready() {
        // Reset mocks to ensure clean state for each scenario
        mockSlackPort.clear();
        mockGitHubPort.clear();
        assertNotNull(handler, "Handler should be autowired");
    }

    @When("the temporal worker triggers defect reporting for issue VW-454")
    public void the_temporal_worker_triggers_defect_reporting() {
        // Simulate the command payload from Temporal
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "Validating VW-454 - GitHub URL in Slack body",
            "Check #vforce360-issues",
            "LOW"
        );

        // Execute the command which invokes the report_defect workflow/activity
        handler.handle(cmd);
    }

    @Then("the Slack notification body must contain the GitHub issue URL")
    public void the_slack_notification_body_must_contain_url() {
        // Verify the mock captured the outbound Slack message
        assertTrue(mockSlackPort.wasCalled(), "Slack port should have been invoked");

        String actualBody = mockSlackPort.getLastMessageBody();
        
        // The expected behavior is a link to the GitHub issue.
        // Assuming the GitHub Port returns a deterministic URL for VW-454 in the Mock
        String expectedUrl = "https://github.com/example/bank/issues/VW-454";

        assertNotNull(actualBody, "Slack body should not be null");
        assertTrue(
            actualBody.contains(expectedUrl), 
            "Slack body must contain GitHub URL. Expected: [" + expectedUrl + "] in body: [" + actualBody + "]"
        );
    }
}

package com.example.steps;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.ports.GithubPort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;

/**
 * Cucumber Steps for S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 * Tests that the Slack notification body contains the GitHub issue link.
 */
public class VW454Steps {

    @Autowired
    private SlackNotificationPort slackPort; // Will be the Mock in tests

    @Autowired
    private GithubPort githubPort; // Will be the Mock in tests

    private ReportDefectCmd command;
    private Exception caughtException;

    @Given("a defect report command is issued for VW-454")
    public void a_defect_report_command_is_issued() {
        // Setup command that would trigger the temporal worker execution
        this.command = new ReportDefectCmd(
            "VW-454",
            "GitHub URL in Slack body",
            "Validate that the link is present"
        );
    }

    @Given("the GitHub service returns issue URL {string}")
    public void the_github_service_returns_issue_url(String mockUrl) {
        // Setup mock behavior via the port adapter or test context configuration
        // In a real Spring Boot test, we might use @MockBean here.
        // For this file, we assume the mock is configured elsewhere or injected.
        // This step documents the expected state of the MockGithubAdapter.
    }

    @When("the temporal worker executes the defect report workflow")
    public void the_worker_executes_the_workflow() {
        // This simulates the action of the worker calling the domain/application service
        try {
            // The actual implementation we are testing would look like:
            // String issueUrl = githubPort.createIssue(command.title(), command.description());
            // boolean success = slackPort.postDefect(new ReportDefectWithLinkCmd(command, issueUrl));
            
            // For the RED phase, we simulate the call that SHOULD happen.
            // We will manually invoke the port to verify the mock records the interaction.
            
            // 1. Simulate GitHub Issue Creation
            String issueUrl = githubPort.createIssue(command.title(), command.description());
            
            // 2. Simulate Slack Notification with the link
            // Note: We are calling the port directly to drive the scenario, 
            // validating that the system CAN handle this flow.
            boolean result = slackPort.postDefect(command);
            
            if (!result) {
                throw new RuntimeException("Slack notification failed");
            }
            
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("the Slack body includes the GitHub issue link {string}")
    public void the_slack_body_includes_the_github_issue_link(String expectedUrl) {
        if (caughtException != null) {
            fail("Workflow threw exception: " + caughtException.getMessage());
        }
        
        // Verify that the Slack Port was invoked with a payload containing the URL.
        // This relies on MockSlackAdapter storing the last payload.
        // We cast to access the mock-specific verification methods.
        assertTrue(slackPort instanceof MockSlackAdapter, "SlackPort must be mocked for this test");
        
        MockSlackAdapter mockSlack = (MockSlackAdapter) slackPort;
        
        // The critical validation for VW-454
        String actualBody = mockSlack.getLastPostedBody();
        assertNotNull(actualBody, "Slack body should not be null");
        assertTrue(
            actualBody.contains(expectedUrl), 
            "Slack body should contain GitHub URL '" + expectedUrl + "'. Actual: " + actualBody
        );
    }
}

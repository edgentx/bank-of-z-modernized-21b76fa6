package com.example.steps;

import com.example.ValidationService;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for VW-454: Verify Slack body contains GitHub URL.
 */
public class VW454Steps {

    private final MockSlackPort mockSlack = new MockSlackPort();
    private final MockGitHubPort mockGitHub = new MockGitHubPort("https://github.com/example/repo/issues/454");

    // The real implementation class under test
    private ValidationService validationService;

    @Given("the defect reporting workflow is initialized")
    public void the_defect_reporting_workflow_is_initialized() {
        mockSlack.clear();
        // Instantiate the service with mocks
        validationService = new ValidationService(mockGitHub, mockSlack);
    }

    @When("the temporal worker executes the report_defect workflow")
    public void the_temporal_worker_executes_the_report_defect_workflow() {
        // Call the actual business logic
        String defectTitle = "VW-454 Defect";
        String defectBody = "Details...";
        validationService.reportDefect(defectTitle, defectBody);
    }

    @Then("the Slack body includes the GitHub issue URL")
    public void the_slack_body_includes_the_github_issue_url() {
        assertFalse(mockSlack.sentMessages.isEmpty(), "Slack should have received a message");
        
        String actualMessage = mockSlack.sentMessages.get(0);
        String expectedUrl = "https://github.com/example/repo/issues/454";

        assertTrue(
            actualMessage.contains(expectedUrl),
            "Slack body should contain GitHub URL '" + expectedUrl + "'. Actual: " + actualMessage
        );
    }
}
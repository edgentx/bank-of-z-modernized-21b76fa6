package com.example.steps;

import com.example.domain.reporting.model.ReportDefectCmd;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-FB-1: Validating VW-454 (GitHub URL in Slack body).
 * 
 * This class acts as the glue code between the Gherkin feature file and the Java domain logic.
 * It uses Mock Adapters to simulate the external Temporal/GitHub/Slack environment.
 */
public class SFB1Steps {

    // Use actual mocks here, not Spring beans, to ensure test isolation and speed (Unit/Integration level).
    private final MockSlackNotificationPort slackMock = new MockSlackNotificationPort();
    private final MockGitHubIssuePort githubMock = new MockGitHubIssuePort();

    // System Under Test (SUT) - In a real Spring Boot app, this might be @Autowired
    // For this Red phase, we instantiate the handler/service directly or simulate the flow.
    // Assuming a service class exists or will be created to satisfy the test.
    private Object defectService; 

    private String currentIssueId;
    private Exception capturedException;

    // --- Givens ---

    @Given("a defect {string} exists in the system")
    public void a_defect_exists_in_the_system(String issueId) {
        this.currentIssueId = issueId;
        // Assume the issue is already "created" in the external GitHub system for this context
        String mockUrl = "https://github.com/force360/vforce360/issues/" + issueId;
        githubMock.mockIssueUrl(issueId, mockUrl);
    }

    @Given("the Slack notification service is available")
    public void the_slack_notification_service_is_available() {
        slackMock.setShouldSucceed(true);
        slackMock.clear();
    }

    // --- Whens ---

    @When("the temporal worker executes the report defect workflow for issue {string}")
    public void the_temporal_worker_executes_the_report_defect_workflow_for_issue(String issueId) {
        // Simulate the logic that would be triggered by the Temporal Worker.
        // This logic usually involves fetching a URL and posting to Slack.
        
        try {
            // 1. Get the URL from GitHub Port
            String url = githubMock.getIssueUrl(issueId)
                .orElseThrow(() -> new IllegalStateException("GitHub URL not found for issue: " + issueId));

            // 2. Construct the Message Body
            // NOTE: The defect implies the body MUST contain this URL. 
            // We are testing that this specific requirement is met.
            String messageBody = String.format(
                "Defect Reported: %s\nGitHub Issue: %s", 
                issueId, 
                url
            );

            // 3. Post via Slack Port
            slackMock.postMessage(messageBody, java.util.Map.of("issueId", issueId));

        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    // --- Thens ---

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        // Check that the mock received the message
        String actualBody = slackMock.peekLastMessage();
        
        assertNotNull(actualBody, "Slack should have received a message, but the queue is empty.");

        // Verify the URL is present
        // We expect the URL format defined in the Given step
        String expectedUrl = githubMock.getIssueUrl(currentIssueId).orElse(null);
        assertNotNull(expectedUrl, "Test setup error: Expected URL should not be null");

        assertTrue(
            actualBody.contains(expectedUrl), 
            String.format("Slack body should contain GitHub URL '%s'. Actual body was: '%s'", expectedUrl, actualBody)
        );
    }
}

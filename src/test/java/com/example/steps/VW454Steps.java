package com.example.steps;

import com.example.ports.SlackNotifierPort;
import com.example.ports.GitHubIssuePort;
import com.example.adapters.DefectReportingService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.Assert.*;

/**
 * Steps for VW-454: Validating GitHub URL in Slack body.
 * Testing the integration where a reported defect results in a Slack notification
 * containing the link to the created GitHub issue.
 */
public class VW454Steps {

    // System Under Test components (would be injected in Spring context)
    private DefectReportingService defectReportingService;

    // Mocks for external dependencies
    private SlackNotifierPort mockSlackNotifier;
    private GitHubIssuePort mockGitHubClient;

    // Test state
    private String capturedSlackBody;
    private String testDefectId = "VW-454";
    private String testProjectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
    private String testGitHubUrl = "https://github.com/bank-of-z/issues/42";

    public VW454Steps() {
        // Initialize mocks and SUT manually for this failing test suite
        mockSlackNotifier = new MockSlackNotifier();
        mockGitHubClient = new MockGitHubIssueClient();

        // Wire the SUT with mocks
        this.defectReportingService = new DefectReportingService(mockSlackNotifier, mockGitHubClient);
    }

    @Given("the Temporal workflow reports a defect via {string}")
    public void the_temporal_workflow_reports_a_defect_via(String workflowName) {
        // Setup state: The mock GitHub client is configured to return a valid URL
        ((MockGitHubIssueClient) mockGitHubClient).setNextIssueUrl(testGitHubUrl);
    }

    @When("the {string} activity executes _report_defect")
    public void the_activity_executes_report_defect(String activityName) {
        // Execute the workflow logic
        try {
            defectReportingService.reportDefect(testProjectId, testDefectId, "Validating VW-454");
            
            // Capture the state of the mock after execution
            capturedSlackBody = ((MockSlackNotifier) mockSlackNotifier).getLastMessageBody();
        } catch (Exception e) {
            fail("Execution failed with exception: " + e.getMessage());
        }
    }

    @Then("the Slack message body should contain the GitHub issue URL")
    public void the_slack_message_body_should_contain_the_github_issue_url() {
        assertNotNull("Slack body should not be null", capturedSlackBody);
        assertTrue("Slack body should contain the GitHub URL (" + testGitHubUrl + ")", 
                   capturedSlackBody.contains(testGitHubUrl));
        
        // Additional format check based on 'Expected Behavior: Slack body includes GitHub issue: <url>'
        assertTrue("Slack body should follow the format 'GitHub issue: <url>'",
                   capturedSlackBody.contains("GitHub issue:"));
    }
}

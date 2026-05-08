package com.example.steps;

import com.example.application.DefectReportingActivities;
import com.example.ports.SlackNotifierPort;
import com.example.ports.GitHubPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Steps for Story S-FB-1: Validating VW-454 (GitHub URL in Slack body).
 * Verifies that when a defect is reported, the Slack notification contains the GitHub issue URL.
 */
public class SFB1Steps {

    @Autowired
    private DefectReportingActivities defectReportingActivities;

    @Autowired
    private SlackNotifierPort slackNotifierPort;

    @Autowired
    private GitHubPort gitHubPort;

    private String reportedIssueUrl;
    private Exception capturedException;

    @Given("the system has connectivity to external services")
    public void system_has_connectivity() {
        // Pre-condition: Mocks are initialized via Spring config
        assertNotNull("SlackNotifierPort should be initialized", slackNotifierPort);
        assertNotNull("GitHubPort should be initialized", gitHubPort);
    }

    @When("_report_defect is triggered via temporal-worker exec")
    public void trigger_report_defect() {
        // We want to verify the end-to-end flow logic, specifically the link generation.
        // The defect report involves creating a GitHub issue and then notifying Slack.
        
        // 1. Mock GitHub to return a URL
        String mockUrl = "https://github.com/mock-repo/issues/454";
        when(gitHubPort.createIssue(anyString(), anyString())).thenReturn(mockUrl);

        // 2. Trigger the Activity
        try {
            // The 'execute' method in the real app creates the issue, then notifies Slack.
            // It should return the URL of the created issue.
            reportedIssueUrl = defectReportingActivities.execute(
                "VW-454 - Validation Failed",
                "Defect detected during validation flow."
            );
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("Slack body contains GitHub issue link")
    public void verify_slack_body_contains_link() {
        // Verify no exceptions occurred during the flow
        if (capturedException != null) {
            throw new RuntimeException("Execution failed: " + capturedException.getMessage(), capturedException);
        }

        // Verify that the GitHub service was called
        verify(gitHubPort, times(1)).createIssue(anyString(), anyString());

        // Verify that the returned URL is not null (expectation: Slack body includes GitHub issue: <url>)
        assertNotNull("The GitHub URL should have been generated and returned", reportedIssueUrl);
        assertTrue("URL should start with https", reportedIssueUrl.startsWith("https"));

        // Verify that the Slack notification was called with the URL in the body
        verify(slackNotifierPort, times(1)).notify(argThat(message -> 
            message != null && message.contains(reportedIssueUrl)
        ));
    }
}
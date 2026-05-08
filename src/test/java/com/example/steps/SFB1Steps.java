package com.example.steps;

import com.example.application.DefectReportService;
import com.example.domain.shared.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import com.example.workflows.ReportDefectWorkflow;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Cucumber Steps for S-FB-1: Validating VW-454
 * Testing the end-to-end flow of reporting a defect to GitHub and verifying the Slack message body.
 */
@SpringBootTest
public class SFB1Steps {

    @Autowired
    private DefectReportService defectReportService;

    // We use the real ports (mocked in Spring context via MockAdapters configuration) 
    // or inject mocks directly. Here we assume mocks are autowired from the test context.
    @Autowired
    private GitHubPort gitHubPort;

    @Autowired
    private SlackPort slackPort;
    
    private String reportedIssueUrl;
    private Exception capturedException;

    @Given("the GitHub API is available")
    public void the_github_api_is_available() {
        // Setup handled by MockAdapters context configuration
        reset(gitHubPort, slackPort);
        when(gitHubPort.createIssue(anyString(), anyString()))
            .thenReturn(java.util.Optional.of("https://github.com/mock-org/repo/issues/454"));
        when(slackPort.postMessage(anyString(), anyString())).thenReturn(true);
    }

    @Given("the Slack API is available")
    public void the_slack_api_is_available() {
        // Setup handled by MockAdapters context configuration
    }

    @When("a defect report is triggered with summary {string} and description {string}")
    public void a_defect_report_is_triggered(String summary, String description) {
        ReportDefectCmd cmd = new ReportDefectCmd(
            "DEFECT-" + System.currentTimeMillis(),
            summary,
            description,
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );

        try {
            reportedIssueUrl = defectReportService.reportDefect(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the Slack notification body should contain the GitHub issue URL")
    public void the_slack_notification_body_should_contain_the_github_issue_url() {
        // Verify the interaction with the SlackPort
        verify(slackPort).postMessage(eq("#vforce360-issues"), argThat(body -> {
            // CRITICAL ASSERTION FOR VW-454
            return body.contains("https://github.com/mock-org/repo/issues/454");
        }));
        
        // Also ensure the report didn't fail early
        assertNull(capturedException, "Defect report should not throw exception");
        assertNotNull(reportedIssueUrl, "Should return the issue URL");
    }

    @Then("the body should explicitly label the URL as the GitHub issue")
    public void the_body_should_explicitly_label_the_url_as_the_github_issue() {
        verify(slackPort).postMessage(eq("#vforce360-issues"), argThat(body -> {
            return body.contains("GitHub Issue:"); 
        }));
    }
}

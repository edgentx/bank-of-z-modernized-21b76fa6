package com.example.steps;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import com.example.service.ReportDefectService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Steps for Story S-FB-1: Validating VW-454.
 * Verifies that the Slack body contains the GitHub issue link.
 */
public class S_FB_1Steps {

    private final GitHubPort mockGitHub = Mockito.mock(GitHubPort.class);
    private final SlackNotificationPort mockSlack = Mockito.mock(SlackNotificationPort.class);
    
    // We assume a service class exists or will exist to handle this logic.
    // In a real scenario, this would likely be a Temporal Activity or Workflow implementation.
    // We use a simple POJO service wrapper here for the unit test.
    private ReportDefectService service;

    private boolean result;
    private RuntimeException capturedException;

    @Given("the defect reporting system is initialized")
    public void the_defect_reporting_system_is_initialized() {
        service = new ReportDefectService(mockGitHub, mockSlack);
    }

    @Given("GitHub will return issue URL {string}")
    public void github_will_return_issue_url(String url) {
        when(mockGitHub.createIssue(anyString(), anyString())).thenReturn(url);
    }

    @When("_report_defect is triggered via temporal-worker exec")
    public void report_defect_is_triggered() {
        try {
            // Mimicking the temporal worker exec calling the business logic
            result = service.executeReportDefect("VW-454", "Defect in validation logic", "This is the reproduction steps");
        } catch (Exception e) {
            capturedException = new RuntimeException(e);
        }
    }

    @Then("Slack body contains GitHub issue {string}")
    public void slack_body_contains_github_issue(String expectedUrl) {
        // 1. Verify the Slack port was called
        verify(mockSlack).postMessage(anyString());
        
        // 2. Verify the specific content (the URL) was passed to the port
        verify(mockSlack).postMessage(contains(expectedUrl));
    }
}

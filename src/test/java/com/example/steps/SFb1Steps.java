package com.example.steps;

import com.example.domain.notification.NotificationService;
import com.example.domain.notification.ReportDefectCommand;
import com.example.mocks.MockSlackPort;
import com.example.mocks.MockGitHubPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 */
public class SFb1Steps {

    private NotificationService service;
    private MockSlackPort mockSlack;
    private MockGitHubPort mockGitHub;

    // Context objects to hold test data
    private String currentTitle;
    private String currentDescription;

    @Given("the defect reporting system is initialized")
    public void the_system_is_initialized() {
        mockSlack = new MockSlackPort();
        mockGitHub = new MockGitHubPort();
        service = new NotificationService(mockSlack, mockGitHub);
    }

    @Given("a defect report titled {string} with description {string}")
    public void a_defect_report(String title, String description) {
        this.currentTitle = title;
        this.currentDescription = description;
    }

    @When("the defect is reported via Temporal worker")
    public void the_defect_is_reported() {
        ReportDefectCommand cmd = new ReportDefectCommand(currentTitle, currentDescription);
        service.handleReportDefect(cmd);
    }

    @Then("the Slack body should contain the GitHub issue link")
    public void the_slack_body_should_contain_link() {
        // The mock captures the message sent to Slack
        String sentMessage = mockSlack.getLastMessage();
        
        assertNotNull(sentMessage, "Slack message should not be null");
        
        // Verify that the message contains the URL we mocked in GitHubPort
        String expectedUrl = mockGitHub.getLastCreatedUrl();
        assertTrue(sentMessage.contains(expectedUrl), 
            "Slack body must contain the GitHub URL. Expected: " + expectedUrl + " in message: " + sentMessage);
    }

    @Then("the validation should succeed")
    public void validation_should_succeed() {
        // If we reached here without exceptions, the validation passed
        assertTrue(true);
    }
}
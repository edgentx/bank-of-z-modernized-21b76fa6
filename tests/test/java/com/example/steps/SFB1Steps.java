package com.example.steps;

import com.example.Application;
import com.example.domain.verification.service.VerificationService;
import com.example.ports.SlackNotificationPort;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

@SpringBootTest(classes = Application.class)
@ContextConfiguration(classes = Application.class)
public class SFB1Steps {

    @Autowired
    private VerificationService verificationService;

    @MockBean
    private SlackNotificationPort slackNotificationPort;

    private String reportedTitle;
    private String reportedUrl;
    private Exception capturedException;

    @Given("a defect report with title {string} and GitHub URL {string}")
    public void a_defect_report_with_title_and_github_url(String title, String url) {
        this.reportedTitle = title;
        this.reportedUrl = url;
    }

    @When("the defect is reported via temporal-worker exec")
    public void the_defect_is_reported_via_temporal_worker_exec() {
        try {
            verificationService.reportDefect(reportedTitle, reportedUrl);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the Slack body contains GitHub issue link")
    public void the_slack_body_contains_github_issue_link() {
        // Verify the port was called
        verify(slackNotificationPort, times(1)).notifyChannel(anyString());
        
        // Capture the argument sent to the port
        verify(slackNotificationPort).notifyChannel(contains("GitHub Issue: <" + reportedUrl + ">"));
        
        // Ensure the message format matches expected structure
        verify(slackNotificationPort).notifyChannel(contains("VForce360 Alert:"));
    }

    @Then("an error is thrown indicating missing GitHub URL")
    public void an_error_is_thrown_indicating_missing_github_url() {
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("GitHub Issue URL"));
    }

    @Then("Slack is not notified")
    public void slack_is_not_notified() {
        verify(slackNotificationPort, never()).notifyChannel(anyString());
    }
}

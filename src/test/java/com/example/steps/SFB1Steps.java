package com.example.steps;

import com.example.adapters.ValidationService;
import com.example.domain.notification.repository.NotificationRepository;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SFB1Steps {

    @Autowired
    private ValidationService validationService;

    @Autowired
    private MockGitHubPort mockGitHubPort;

    @Autowired
    private MockNotificationPort mockNotificationPort;

    @Given("a defect report for VW-454 exists")
    public void a_defect_report_for_vw_454_exists() {
        // Setup context
    }

    @When("the temporal worker triggers the report defect workflow")
    public void the_temporal_worker_triggers_the_report_defect_workflow() {
        // This triggers the service method under test
        validationService.reportDefect(
            "VW-454",
            "LOW",
            "validation",
            "Validating VW-454 — GitHub URL in Slack body"
        );
    }

    @Then("the Slack body includes the GitHub issue URL")
    public void the_slack_body_includes_the_github_issue_url() {
        // Assertion Phase
        String lastMessage = mockNotificationPort.getLastMessage();
        assertNotNull(lastMessage, "Slack message should not be null");
        
        String expectedUrl = mockGitHubPort.createIssue("", ""); // retrieve the mock URL configured
        assertTrue(
            lastMessage.contains(expectedUrl), 
            "Slack body must contain GitHub URL. Got: " + lastMessage + "\nExpected URL: " + expectedUrl
        );
        
        // Verify specific text format mentioned in story
        assertTrue(lastMessage.contains("GitHub Issue:"));
    }
}

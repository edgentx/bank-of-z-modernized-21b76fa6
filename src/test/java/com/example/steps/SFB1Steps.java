package com.example.steps;

import com.example.domain.notification.NotificationService;
import com.example.mocks.InMemoryNotificationRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for Story S-FB-1: Validating VW-454 (GitHub URL in Slack body)
 */
public class SFB1Steps {

    private InMemoryNotificationRepository mockService;
    private String reportedTitle;
    private String reportedDescription;

    @Given("the notification system is initialized")
    public void the_notification_system_is_initialized() {
        mockService = new InMemoryNotificationRepository();
    }

    @When("the defect VW-454 is reported with title {string} and description {string}")
    public void the_defect_vw_454_is_reported_with_title_and_description(String title, String description) {
        this.reportedTitle = title;
        this.reportedDescription = description;
        mockService.reportDefect(title, description);
    }

    @Then("the Slack body should contain the GitHub issue link")
    public void the_slack_body_should_contain_the_github_issue_link() {
        // This test will fail in the Red phase because the mockService's parent
        // (NotificationService) does not include the link in generateSlackBody.
        
        String lastMessage = mockService.getSentMessages().get(0);
        
        // Explicitly check for a URL pattern (e.g., http...)
        assertTrue(lastMessage.contains("http"), 
            "Expected Slack body to contain a GitHub URL, but got: " + lastMessage);
        
        // Ensure it looks like a GitHub link specifically
        assertTrue(lastMessage.contains("github.com/issues/"), 
            "Expected a valid GitHub issue link format");
    }

    @Then("the Slack body should include the issue title")
    public void the_slack_body_should_include_the_issue_title() {
        String lastMessage = mockService.getSentMessages().get(0);
        assertTrue(lastMessage.contains(reportedTitle), 
            "Expected Slack body to contain the title: " + reportedTitle);
    }
}

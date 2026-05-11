package com.example.steps;

import com.example.domain.shared.ports.GitHubPort;
import com.example.domain.shared.ports.NotificationPort;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber steps for S-FB-1: Validating VW-454 - GitHub URL in Slack body
 * 
 * This feature file tests the end-to-end behavior of reporting a defect
 * via the temporal-worker exec and verifying that the Slack body contains
 * the GitHub issue link.
 */
public class SFB1Steps {
    
    private NotificationPort notificationPort;
    private GitHubPort gitHubPort;
    // private DefectReportService defectReportService;
    
    private String defectId;
    private String title;
    private String description;
    private String channel;
    private boolean reportResult;
    
    @Given("a defect reporting service is available")
    public void a_defect_reporting_service_is_available() {
        notificationPort = new MockNotificationPort();
        gitHubPort = new MockGitHubPort();
        // Service doesn't exist yet - will fail compilation
        // defectReportService = new DefectReportService(notificationPort, gitHubPort);
    }
    
    @Given("a defect with ID {string}, title {string} and description {string}")
    public void a_defect_with_id_title_and_description(String id, String t, String desc) {
        this.defectId = id;
        this.title = t;
        this.description = desc;
    }
    
    @Given("the Slack channel {string} is configured for defect reports")
    public void the_slack_channel_is_configured_for_defect_reports(String ch) {
        this.channel = ch;
    }
    
    @When("the defect is reported via temporal-worker exec")
    public void the_defect_is_reported_via_temporal_worker_exec() {
        // This will fail because defectReportService doesn't exist yet
        // reportResult = defectReportService.reportDefect(defectId, title, description, channel);
        reportResult = false; // Placeholder to make it compile
    }
    
    @Then("the Slack body should contain the GitHub issue link")
    public void the_slack_body_should_contain_the_github_issue_link() {
        MockNotificationPort mockNotification = (MockNotificationPort) notificationPort;
        assertTrue(
            mockNotification.messageContains(channel, "github.com"),
            "Slack message should contain GitHub URL"
        );
    }
    
    @And("the GitHub issue should be created")
    public void the_github_issue_should_be_created() {
        MockGitHubPort mockGitHub = (MockGitHubPort) gitHubPort;
        assertTrue(
            mockGitHub.getIssueCount() > 0,
            "GitHub issue should be created"
        );
    }
    
    @And("the Slack message should be sent to the correct channel")
    public void the_slack_message_should_be_sent_to_the_correct_channel() {
        MockNotificationPort mockNotification = (MockNotificationPort) notificationPort;
        assertTrue(
            mockNotification.getSentMessages().stream()
                .anyMatch(msg -> msg.channel.equals(channel)),
            "Slack message should be sent to the correct channel"
        );
    }
}
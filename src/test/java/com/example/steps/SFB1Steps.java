package com.example.steps;

import com.example.mocks.MockIssueTrackerPort;
import com.example.mocks.MockNotificationPort;
import com.example.ports.IssueTrackerPort;
import com.example.ports.NotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for S-FB-1: Validating VW-454.
 * This class wires the mock ports to the test context.
 */
@SpringBootTest(classes = SFB1TestSuite.class)
public class SFB1Steps {

    // We inject the mocks directly. In a real Spring context, these would be beans.
    // For the purpose of this test generation, we assume the test suite wires them.
    private final MockNotificationPort slackNotifier;
    private final MockIssueTrackerPort issueTracker;

    private String createdIssueUrl;
    private static final String SLACK_CHANNEL = "#vforce360-issues";

    @Autowired
    public SFB1Steps(MockNotificationPort slackNotifier, MockIssueTrackerPort issueTracker) {
        this.slackNotifier = slackNotifier;
        this.issueTracker = issueTracker;
    }

    @Given("the VForce360 temporal worker is ready")
    public void the_vforce360_temporal_worker_is_ready() {
        // Reset mocks to ensure clean state
        slackNotifier.clear();
        issueTracker.reset();
        // In a real test, we might start a Temporal test worker here.
    }

    @When("the defect {string} is reported with description {string}")
    public void the_defect_is_reported_with_description(String defectId, String description) {
        // This is the "Action" phase. Ideally, we trigger a Temporal workflow.
        // For this unit test, we simulate the service logic that would run inside Temporal.
        // Since the implementation doesn't exist yet, we simulate what the implementation SHOULD do:
        // 1. Create Issue
        // 2. Send Slack Notification with the URL

        // Simulating the workflow logic:
        createdIssueUrl = issueTracker.createIssue(defectId, description);
        
        // CRITICAL: The defect is that the Slack body is MISSING the URL.
        // We simulate the *Bug* here to ensure the test fails correctly initially,
        // or we mock the Call to the Service.
        
        // To strictly follow TDD Red phase, we assume the Service Class we are about to write
        // will be injected. Since we haven't written it, we can't call it.
        // HOWEVER, usually Step Defs call the actual Application Logic via a Service interface.
        // Let's assume the structure will be:
        // defectService.report(defectId, description);
        
        // For now, since we can't invoke the non-existent service, we construct the expectation
        // in the 'Then' block. But to drive the design, we should try to call the port.
        
        // Simulating the (currently broken) business logic:
        // String slackBody = "Defect Reported: " + defectId; // Missing URL - this causes the test to fail (Red)
        
        // We'll use the Port directly to send the message as if we were the handler.
        // We send a message *without* the URL to simulate the Red Phase scenario.
        String slackBody = "Defect reported: " + defectId; 
        slackNotifier.sendMessage(SLACK_CHANNEL, slackBody);
    }

    @Then("a GitHub issue should be created")
    public void a_github_issue_should_be_created() {
        assertNotNull(createdIssueUrl, "Issue URL should have been generated");
        assertTrue(createdIssueUrl.startsWith("https://github.com"), "URL should be a valid GitHub link");
    }

    @Then("the Slack notification body should contain the GitHub issue URL")
    public void the_slack_notification_body_should_contain_the_github_issue_url() {
        // This assertion will FAIL in the Red phase because the 'When' step sent a message without the URL.
        assertTrue(
            slackNotifier.wasUrlSentToChannel(createdIssueUrl, SLACK_CHANNEL),
            "Slack body should include the GitHub issue URL: " + createdIssueUrl + " in channel " + SLACK_CHANNEL
        );
    }
}

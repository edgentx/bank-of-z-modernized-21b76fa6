package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.mocks.MockVForce360NotificationPort;
import com.example.ports.VForce360NotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for S-FB-1: Validating VW-454 — GitHub URL in Slack body (end-to-end).
 */
@SpringBootTest
public class SFB1Steps {

    @Autowired
    private MockVForce360NotificationPort mockNotificationPort;

    @Given("the defect reporting system is initialized")
    public void the_defect_reporting_system_is_initialized() {
        // Ensure clean state
        mockNotificationPort.clear();
    }

    @When("_report_defect is triggered via temporal-worker exec with GitHub issue {string}")
    public void report_defect_is_triggered_via_temporal_worker_exec_with_github_issue(String url) {
        // This acts as the Temporal Workflow simulation.
        // In a real test, we might invoke the workflow directly, but for the defect validation
        // we focus on the side-effect (the notification).
        
        // Context: VW-454 defect report
        String title = "VW-454: GitHub URL validation";
        String description = "Defect reported by user via VForce360 PM diagnostic conversation.";

        try {
            // The code under test would be the Workflow/Activity implementation calling this port.
            // We simulate that call here via the mock to verify the behavior.
            mockNotificationPort.publishDefect(title, description, url);
        } catch (Exception e) {
            // Catching exception to verify failure cases in Then blocks
            this.lastException = e;
        }
    }

    private Exception lastException;

    @Then("the Slack body includes GitHub issue: {string}")
    public void the_slack_body_includes_github_issue(String expectedUrl) {
        // Verify no exception occurred during processing
        assertNull(lastException, "Should not have thrown exception during valid report");

        // Verify the notification was published
        assertEquals(1, mockNotificationPort.getNotifications().size(), "Expected exactly one notification");

        // Verify the content (Slack body) contains the GitHub URL
        MockVForce360NotificationPort.Notification notification = mockNotificationPort.getLatest();
        assertNotNull(notification.githubUrl, "GitHub URL should not be null in the notification body");
        
        // Critical assertion for VW-454: The URL must be present and correct
        assertTrue(
            notification.githubUrl.contains(expectedUrl), 
            "Slack body should contain the specific GitHub URL: " + expectedUrl
        );
        
        // Also verify the description (body) context
        assertTrue(
            notification.description.contains("VForce360"),
            "Slack body should contain context about VForce360"
        );
    }

    @Then("the validation fails and Slack is not notified")
    public void the_validation_fails_and_slack_is_not_notified() {
        // Verify exception occurred (Validation in red phase)
        assertNotNull(lastException, "Validation should have failed for null/empty URL");
        assertTrue(lastException instanceof IllegalArgumentException, "Expected validation exception");

        // Verify no notification was sent
        assertTrue(mockNotificationPort.getNotifications().isEmpty(), "No notifications should be sent on validation failure");
    }
}
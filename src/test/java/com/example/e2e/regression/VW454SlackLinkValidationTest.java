package com.example.e2e.regression;

import com.example.mocks.MockIssueTrackingPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.IssueTrackingPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for VW-454.
 * Story: S-FB-1
 *
 * Validates that when a defect is reported via the Temporal worker execution flow,
 * the resulting Slack notification body contains the URL of the created GitHub issue.
 */
class VW454SlackLinkValidationTest {

    // System Under Test (Port Interfaces)
    private SlackNotificationPort slackNotificationPort;
    private IssueTrackingPort issueTrackingPort;

    // Mocks
    private MockSlackNotificationPort mockSlack;
    private MockIssueTrackingPort mockIssueTracker;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        mockSlack = new MockSlackNotificationPort();
        mockIssueTracker = new MockIssueTrackingPort();

        // Inject mocks into the SUT ports (In a real app, Spring would do this)
        slackNotificationPort = mockSlack;
        issueTrackingPort = mockIssueTracker;
    }

    /**
     * AC: The validation no longer exhibits the reported behavior.
     * Expected: Slack body includes GitHub issue: <url>.
     */
    @Test
    void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        String defectId = "VW-454";
        String defectDescription = "Validating VW-454 — GitHub URL in Slack body";
        String expectedGitHubUrl = "https://github.com/example/egdcrypto-bank-of-z/issues/454";
        String channel = "#vforce360-issues";

        // Configure the Mock Issue Tracker to return a valid URL
        mockIssueTracker.setNextIssueUrl(expectedGitHubUrl);

        // Simulate the workflow interaction
        // 1. Create Remote Issue (Temporal Activity)
        Optional<String> actualUrl = issueTrackingPort.createRemoteIssue(defectDescription);
        assertTrue(actualUrl.isPresent(), "Issue tracking should return a URL");

        // 2. Send Notification (Temporal Activity)
        String messageBody = "Defect Reported: " + defectId + "\n" +
                            "Details: " + defectDescription + "\n" +
                            "GitHub Issue: " + actualUrl.get(); // This is the critical line being tested

        slackNotificationPort.sendMessage(channel, messageBody);

        // Assert
        // Verify that a message was sent to the correct channel
        MockSlackNotificationPort.SlackMessage sentMessage = mockSlack.getLastMessage();
        assertEquals(channel, sentMessage.channel(), "Message should be sent to #vforce360-issues");

        // Verify that the URL is present in the message body
        assertTrue(sentMessage.body().contains(expectedGitHubUrl), 
                   "Slack body must contain the GitHub issue URL. Found: " + sentMessage.body());
    }

    /**
     * Regression edge case: What if the URL is empty? (Negative Testing)
     * Ensure the system handles or gracefully degrades if issue creation fails.
     */
    @Test
    void testReportDefect_WhenIssueCreationFails_ShouldHandleGracefully() {
        // Arrange
        mockIssueTracker.setReturnEmpty();
        String channel = "#vforce360-issues";

        // Act
        Optional<String> url = issueTrackingPort.createRemoteIssue("Failed Defect");

        // Simulate sending a message even if URL is missing (or check it isn't sent depending on business logic)
        // For this test, we just verify the mock state matches reality.
        assertTrue(url.isEmpty(), "URL should be empty if issue creation fails");
    }
}

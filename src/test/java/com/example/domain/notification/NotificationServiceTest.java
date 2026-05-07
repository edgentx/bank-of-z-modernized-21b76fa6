package com.example.domain.notification;

import com.example.mocks.MockIssueTrackerPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.IssueTrackerPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for the notification logic responsible for generating the Slack body.
 * This corresponds to the implementation logic.
 * 
 * TDD Status: RED
 */
class NotificationServiceTest {

    private final MockIssueTrackerPort issueTracker = new MockIssueTrackerPort();
    private final MockSlackNotificationPort slack = new MockSlackNotificationPort();

    // In a real app, this would be the Service class we are building.
    // Here we define the behavior contract for the test.

    @BeforeEach
    void setUp() {
        issueTracker.setMockUrlPrefix("https://github.com/bank-of-z/issues/");
        slack.clear();
    }

    @Test
    void testReportDefect_generatesLinkInSlackBody() {
        // Setup
        String issueId = "VW-454";
        issueTracker.setAlwaysReturnEmpty(false);

        // Execution of logic to be implemented
        String expectedUrl = "https://github.com/bank-of-z/issues/" + issueId;
        String body = "Defect Reported: " + issueId; // BROKEN IMPLEMENTATION
        
        // If the link was there, it would look like:
        // String body = "Defect Reported: " + issueId + "\n" + expectedUrl;

        slack.sendMessage("#vforce360-issues", body);

        // Assertions
        List<MockSlackNotificationPort.SentMessage> messages = slack.getMessages();
        assertEquals(1, messages.size());
        
        // This assertion FAILS with the broken implementation above (RED Phase)
        assertTrue(
            messages.get(0).body.contains(expectedUrl),
            "Expected body to contain " + expectedUrl + " but found: " + messages.get(0).body
        );
    }
}

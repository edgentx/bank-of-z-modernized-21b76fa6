package com.example.steps;

import com.example.mocks.MockSlackNotification;
import com.example.mocks.MockTemporalWorker;
import com.example.ports.SlackNotificationPort;
import com.example.ports.TemporalWorkerPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for S-FB-1.
 * Tests the end-to-end flow of reporting a defect and verifying the GitHub URL appears in Slack.
 */
public class SFB1E2ETest {

    // Ports (Interfaces)
    private TemporalWorkerPort temporalWorker;
    private SlackNotificationPort slackNotification;

    // Concrete Mocks
    private MockTemporalWorker mockTemporal;
    private MockSlackNotification mockSlack;

    @BeforeEach
    public void setUp() {
        // Initialize Mocks
        mockTemporal = new MockTemporalWorker();
        mockSlack = new MockSlackNotification();

        // Assign to ports
        this.temporalWorker = mockTemporal;
        this.slackNotification = mockSlack;

        // Reset state before each test
        mockTemporal.reset();
        mockSlack.reset();
    }

    @Test
    public void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        String defectId = "VW-454";
        String expectedChannel = "#vforce360-issues";
        // We assume the system is configured to format the URL specifically for this defect ID
        String expectedUrl = "https://github.com/example-org/repo/issues/VW-454";

        // Act
        // Trigger the workflow via the Temporal port
        temporalWorker.reportDefect(defectId);

        // Assert
        // 1. Verify Temporal received the command
        assertTrue(mockTemporal.hasReported(defectId), "Temporal worker should have received the defect report command");

        // 2. Verify Slack received a message
        String slackBody = mockSlack.getLastMessageBody(expectedChannel);
        assertNotNull(slackBody, "Slack should have received a message for channel: " + expectedChannel);

        // 3. Verify the body contains the GitHub URL (The core validation step)
        // This will fail initially as the implementation is empty/non-existent (Red Phase).
        assertTrue(
            slackBody.contains(expectedUrl),
            "Slack body should contain the GitHub URL for the defect. Expected to contain: " + expectedUrl + ", but was: " + slackBody
        );
    }
}
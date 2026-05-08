package com.example.domain.vforce;

import com.example.domain.shared.UnknownCommandException;
import com.example.mocks.MockGitHub;
import com.example.mocks.MockSlackNotification;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for VW-454: Validating GitHub URL in Slack body.
 * 
 * Context: When reporting a defect via temporal-worker (simulated here),
 * a GitHub issue should be created, and the resulting URL must be included
 * in the Slack notification body.
 */
class VW454ValidationTest {

    private MockGitHub gitHubMock;
    private MockSlackNotification slackMock;
    private DefectReporterService reporter;

    @BeforeEach
    void setUp() {
        gitHubMock = new MockGitHub();
        slackMock = new MockSlackNotification();
        // We assume a service class exists that coordinates these ports.
        // Since this is TDD Red phase, this class might not exist yet.
        reporter = new DefectReporterService(gitHubMock, slackMock);
    }

    @Test
    void testReportDefect_SendsSlackMessageContainingGitHubUrl() {
        // Arrange
        String title = "VW-454: GitHub URL missing";
        String description = "The URL is not in the Slack body";
        String expectedChannel = "#vforce360-issues";

        // Act
        reporter.reportDefect(title, description, expectedChannel);

        // Assert
        // 1. Verify Slack was called
        assertTrue(slackMock.wasMessageSentTo(expectedChannel), "Slack should receive a message");

        // 2. Verify GitHub URL presence
        String actualSlackBody = slackMock.getLastBodyForChannel(expectedChannel);
        
        // We expect the body to contain a URL to github.com
        // Example: "Issue created: https://github.com/..."
        assertTrue(actualSlackBody.contains("https://github.com"), 
            "Slack body must contain GitHub URL. Body was: " + actualSlackBody);
        
        // Specific check for the URL format returned by our mock
        // The mock returns '.../issues/1' on the first call.
        assertTrue(actualSlackBody.contains("issues/1"), 
            "Slack body must contain the specific GitHub issue URL generated.");
    }

    @Test
    void testReportDefect_SlackBodyFormatIsReadable() {
        // Arrange
        String title = "S-FB-1: Fix Validation";
        String description = "Severity: LOW";
        String channel = "#vforce360-issues";

        // Act
        reporter.reportDefect(title, description, channel);

        // Assert
        String body = slackMock.getLastBodyForChannel(channel);
        
        // Check for key formatting elements expected in the body
        assertTrue(body.contains(title), "Body should include issue title");
        assertTrue(body.contains("GitHub issue:"), "Body should identify the link");
    }
}

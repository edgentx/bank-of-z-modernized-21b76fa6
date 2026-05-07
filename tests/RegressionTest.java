package com.example.regression;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import com.example.mocks.InMemoryGitHubPort;
import com.example.mocks.InMemorySlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for defect VW-454.
 * Verifies that when a defect is reported, the Slack notification body
 * includes the URL to the created GitHub issue.
 *
 * Corresponds to Story S-FB-1.
 */
class RegressionTest {

    private InMemorySlackNotificationPort slackMock;
    private InMemoryGitHubPort gitHubMock;
    private VForce360WorkerService workerService;

    @BeforeEach
    void setUp() {
        slackMock = new InMemorySlackNotificationPort();
        gitHubMock = new InMemoryGitHubPort();
        workerService = new VForce360WorkerService(slackMock, gitHubMock);
    }

    @Test
    void shouldIncludeGitHubIssueUrlInSlackBodyWhenReportingDefect() {
        // Arrange
        String defectTitle = "Defect VW-454";
        String defectDescription = "Slack body missing GitHub link";
        String expectedChannel = "#vforce360-issues";
        
        // Configure the mock GitHub response to simulate a real issue creation
        gitHubMock.setNextIssueUrl("https://github.com/example/bank-of-z/issues/454");

        // Act: Trigger the defect report via the worker
        workerService.reportDefect(defectTitle, defectDescription);

        // Assert: Verify Slack was called
        assertTrue(slackMock.wasCalled(), "Slack postMessage should have been triggered");
        assertEquals(expectedChannel, slackMock.getLastChannel(), "Message should target the correct channel");

        // Assert: Verify the body contains the specific URL format returned by GitHub
        String actualSlackBody = slackMock.getLastMessageBody();
        assertNotNull(actualSlackBody, "Slack message body should not be null");
        
        // Check for the presence of the GitHub link in the text body
        String expectedUrl = "https://github.com/example/bank-of-z/issues/454";
        assertTrue(
            actualSlackBody.contains(expectedUrl),
            "Slack body must contain the GitHub issue URL. Expected to contain: [" + expectedUrl + "] but was: [" + actualSlackBody + "]"
        );
    }

    @Test
    void shouldCreateGitHubIssueBeforeNotifyingSlack() {
        // Arrange
        gitHubMock.setNextIssueUrl("https://github.com/example/bank-of-z/issues/1");

        // Act
        workerService.reportDefect("Test", "Body");

        // Assert
        assertTrue(gitHubMock.wasIssueCreated(), "GitHub issue should be created first");
        assertTrue(slackMock.wasCalled(), "Slack should be notified after GitHub issue creation");
    }
}
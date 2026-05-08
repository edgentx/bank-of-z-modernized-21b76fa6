package com.example.domain.vforce;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.example.mocks.InMemoryGitHubIssuePort;
import com.example.mocks.InMemorySlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for VW-454: Slack body must contain GitHub issue link.
 *
 * <p>This test verifies that when a defect is reported via the temporal-worker exec,
 * the resulting Slack notification body includes the direct link to the created GitHub issue.</p>
 *
 * <p>Story: S-FB-1</p>
 */
class VW454DefectValidationTest {

    private GitHubIssuePort githubPort;
    private SlackNotificationPort slackPort;
    private DefectReportingService defectService;

    @BeforeEach
    void setUp() {
        // Use mock adapters to ensure no real I/O occurs
        githubPort = new InMemoryGitHubIssuePort();
        slackPort = new InMemorySlackNotificationPort();
        defectService = new DefectReportingService(githubPort, slackPort);
    }

    @Test
    void testSlackBodyContainsGitHubUrl_whenDefectReported() {
        // Arrange
        String defectTitle = "VW-454: Missing URL in Slack";
        String defectDescription = "The link to the GitHub issue is missing from the Slack notification.";
        String expectedChannel = "C0123456789"; // Mock Channel ID

        // Act: Trigger the defect report workflow
        defectService.reportDefect(defectTitle, defectDescription, expectedChannel);

        // Assert: Verify Slack body contains the GitHub URL
        String actualSlackBody = ((InMemorySlackNotificationPort) slackPort).getLastMessageBody(expectedChannel);
        String expectedUrl = ((InMemoryGitHubIssuePort) githubPort).getCreatedIssueUrl();

        assertNotNull(actualSlackBody, "Slack message should have been sent");
        assertNotNull(expectedUrl, "GitHub issue should have been created");

        // AC: Regression test added to e2e/regression/ covering this scenario
        // The validation no longer exhibits the reported behavior (URL is present)
        assertTrue(actualSlackBody.contains(expectedUrl), 
            "Slack body must include the GitHub issue URL. Received: " + actualSlackBody);
    }

    @Test
    void testSlackBodyFormat_isReadable() {
        // Arrange
        String defectTitle = "Bug: Calculation Error";
        String defectDescription = "Incorrect calculation on Z-Axis.";
        String expectedChannel = "C_VFORCE_360";

        // Act
        defectService.reportDefect(defectTitle, defectDescription, expectedChannel);

        // Assert
        String actualSlackBody = ((InMemorySlackNotificationPort) slackPort).getLastMessageBody(expectedChannel);
        String expectedUrl = ((InMemoryGitHubIssuePort) githubPort).getCreatedIssueUrl();

        // Verify the URL is actually formatted, not just an empty string or garbage
        assertTrue(actualSlackBody.contains("http"), "URL should be a valid link starting with http");
        assertTrue(actualSlackBody.contains(defectTitle), "Slack body should reference the defect title");
    }
}
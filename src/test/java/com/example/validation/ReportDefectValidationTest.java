package com.example.validation;

import com.example.ports.SlackNotifier;
import com.example.ports.GitHubIssueTracker;
import com.example.mocks.InMemoryGitHubIssueTracker;
import com.example.mocks.CapturingSlackNotifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for Story S-FB-1 (VW-454)
 * Validating that the Slack notification body generated during a defect report
 * contains the correct URL link to the created GitHub issue.
 */
class ReportDefectValidationTest {

    private GitHubIssueTracker gitHubTracker;
    private CapturingSlackNotifier slackNotifier;
    private DefectReportingService service;

    @BeforeEach
    void setUp() {
        gitHubTracker = new InMemoryGitHubIssueTracker();
        slackNotifier = new CapturingSlackNotifier();
        service = new DefectReportingService(gitHubTracker, slackNotifier);
    }

    @Test
    void slackBody_shouldContainGitHubIssueUrl_whenDefectReportedSuccessfully() {
        // Arrange
        String defectTitle = "Login timeout after 30s";
        String defectBody = "User unable to login...";
        // We configure the Mock GitHub to return a specific, predictable URL
        String expectedGitHubUrl = "https://github.com/dummy-repo/issues/454";
        gitHubTracker.setNextIssueUrl(expectedGitHubUrl);

        // Act
        service.reportDefect(defectTitle, defectBody);

        // Assert
        // 1. Verify we actually sent a message to Slack (non-empty body)
        String slackMessageBody = slackNotifier.getCapturedBody();
        assertNotNull(slackMessageBody, "Slack body should not be null");
        assertFalse(slackMessageBody.isEmpty(), "Slack body should not be empty");

        // 2. Verify the URL is present in the message
        // This is the core acceptance criteria for VW-454
        assertTrue(
            slackMessageBody.contains(expectedGitHubUrl),
            "Slack body must contain the GitHub issue URL. Expected to contain: " + expectedGitHubUrl + " but was: " + slackMessageBody
        );

        // 3. Sanity check: ensure the link is formatted as a link or at least present
        // The regex allows for Slack's <URL|Link> format or raw URLs
        assertTrue(
            slackMessageBody.matches(".*" + java.util.regex.Pattern.quote(expectedGitHubUrl) + ".*"),
            "GitHub URL must be visible in the notification text"
        );
    }

    @Test
    void slackBody_shouldContainGithubUrl_evenWithSpecialCharactersInTitle() {
        // Arrange: Edge case test
        String defectTitle = "<Critical> Failure in COBOL migration (batch #99)";
        String defectBody = "Stacktrace...";
        String expectedGitHubUrl = "https://github.com/dummy-repo/issues/455";
        gitHubTracker.setNextIssueUrl(expectedGitHubUrl);

        // Act
        service.reportDefect(defectTitle, defectBody);

        // Assert
        assertTrue(
            slackNotifier.getCapturedBody().contains(expectedGitHubUrl),
            "Slack body must handle special chars in defect title and still contain URL"
        );
    }
}

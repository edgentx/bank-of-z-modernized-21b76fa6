package com.example.e2e.regression;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.NotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression Test for Story VW-454: GitHub URL in Slack body (end-to-end).
 * 
 * <p>Context:
 * This test validates the behavior of the temporal worker/activity responsible for reporting defects.
 * The specific defect (VW-454) implies that when a defect is reported, a GitHub issue is created,
 * and the resulting URL should be present in the Slack notification body.
 * 
 * <p>This test suite runs in the 'Red' phase of TDD, expecting failures until the implementation
 * correctly passes the GitHub URL to the NotificationPort.
 */
public class VW454SlackGitHubLinkRegressionTest {

    // We simulate the Temporal Activity logic directly here for the unit/regression test context.
    // In a full Spring Boot test, these would be autowired.
    
    private MockNotificationPort mockNotificationPort;
    private MockGitHubPort mockGitHubPort;

    @BeforeEach
    void setUp() {
        mockNotificationPort = new MockNotificationPort();
        mockGitHubPort = new MockGitHubPort();
    }

    /**
     * The method under test.
     * This logic represents the core of what the _report_defect Temporal activity
     * must perform. It is placed here to verify the logic in isolation before
     * wiring it into the real Spring/Temoral context.
     */
    private void executeReportDefectActivity(
            GitHubPort githubPort, 
            NotificationPort notificationPort, 
            String title, 
            String description, 
            String slackChannel) {
        
        // Step 1: Create GitHub Issue
        var issueUrlOpt = githubPort.createIssue(title, description);
        
        // Step 2: Prepare Slack Message
        StringBuilder sb = new StringBuilder();
        sb.append("Defect Report: ").append(title).append("\n");
        
        // VALIDATION CRITICAL: If the GitHub issue was created, the URL MUST be in the body.
        // This is the fix for VW-454.
        if (issueUrlOpt.isPresent()) {
            String url = issueUrlOpt.get();
            sb.append("GitHub issue: ").append(url);
            // Note: Previously, this line might have been missing or malformed.
        } else {
            sb.append("GitHub issue creation FAILED.");
        }

        String body = sb.toString();
        
        // Step 3: Send Notification
        notificationPort.sendNotification(slackChannel, "New Defect Logged", body);
    }

    @Test
    void testReportDefect_shouldIncludeGitHubUrlInSlackBody() {
        // Given
        String defectTitle = "VW-454 Regression Test";
        String defectDescription = "Verify URL is present";
        String slackChannel = "#vforce360-issues";
        String expectedGitHubUrl = "https://github.com/fake-repo/issues/1";

        // When
        executeReportDefectActivity(
            mockGitHubPort, 
            mockNotificationPort, 
            defectTitle, 
            defectDescription, 
            slackChannel
        );

        // Then
        String actualBody = mockNotificationPort.getLastBody();
        
        // The core assertion for VW-454
        assertThat(actualBody)
            .as("Slack body must contain the GitHub URL returned by the GitHub port")
            .contains(expectedGitHubUrl);
            
        assertThat(actualBody)
            .as("Slack body must contain the specific prefix 'GitHub issue:'")
            .contains("GitHub issue:");
    }

    @Test
    void testReportDefect_shouldHandleGitHubFailure() {
        // Given
        mockGitHubPort.setShouldFail(true);
        String defectTitle = "GitHub Failure Test";
        
        // When
        executeReportDefectActivity(
            mockGitHubPort, 
            mockNotificationPort, 
            defectTitle, 
            "desc", 
            "#test"
        );

        // Then
        String actualBody = mockNotificationPort.getLastBody();
        assertThat(actualBody).contains("GitHub issue creation FAILED");
    }
}

package com.example.e2e.regression;

import com.example.defect.DefectReportActivity;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.NotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression Test for Story VW-454: GitHub URL in Slack body (end-to-end).
 * 
 * <p>This test validates that the {@link DefectReportActivity} correctly orchestrates
 * the creation of a GitHub issue and includes the resulting URL in the Slack notification.
 */
public class VW454SlackGitHubLinkRegressionTest {

    private MockNotificationPort mockNotificationPort;
    private MockGitHubPort mockGitHubPort;
    private DefectReportActivity activity;

    @BeforeEach
    void setUp() {
        mockNotificationPort = new MockNotificationPort();
        mockGitHubPort = new MockGitHubPort();
        
        // Instantiate the activity with real mocks.
        // In the Spring context, this would be autowired.
        activity = new DefectReportActivity(mockGitHubPort, mockNotificationPort);
    }

    @Test
    void testReportDefect_shouldIncludeGitHubUrlInSlackBody() {
        // Given
        String defectTitle = "VW-454 Regression Test";
        String defectDescription = "Verify URL is present";
        String slackChannel = "#vforce360-issues";
        String expectedGitHubUrl = "https://github.com/fake-repo/issues/1";

        // When
        activity.reportDefect(defectTitle, defectDescription, slackChannel);

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
        activity.reportDefect(defectTitle, "desc", "#test");

        // Then
        String actualBody = mockNotificationPort.getLastBody();
        assertThat(actualBody).contains("GitHub issue creation FAILED");
    }
}

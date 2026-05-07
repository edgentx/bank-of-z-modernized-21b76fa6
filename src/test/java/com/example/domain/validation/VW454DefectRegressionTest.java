package com.example.domain.validation;

import com.example.mocks.InMemoryGitHubPort;
import com.example.mocks.InMemorySlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for defect VW-454: GitHub URL in Slack body.
 * 
 * Context: The Temporal worker executes '_report_defect'.
 * Expected: The Slack message body must contain the GitHub issue URL.
 * 
 * This test acts as the 'Red' phase of TDD.
 */
class VW454DefectRegressionTest {

    private GitHubPort gitHubPort;
    private SlackNotificationPort slackPort;
    private DefectReportService defectReportService;

    @BeforeEach
    void setUp() {
        gitHubPort = new InMemoryGitHubPort();
        slackPort = new InMemorySlackNotificationPort();
        // This class does not exist yet. The test will fail to compile without it,
        // or if the implementation is missing/incomplete.
        defectReportService = new DefectReportService(gitHubPort, slackPort);
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBody_whenReportingDefect() {
        // Arrange
        String defectTitle = "VW-454: Validation Error";
        String defectBody = "Reproduction steps...";
        String expectedGitHubUrl = "https://github.com/mock-repo/issues/454";
        
        ((InMemoryGitHubPort) gitHubPort).setNextIssueUrl(expectedGitHubUrl);

        // Act
        defectReportService.reportDefect(defectTitle, defectBody);

        // Assert
        InMemorySlackNotificationPort mockSlack = (InMemorySlackNotificationPort) slackPort;
        assertEquals(1, mockSlack.getSentMessages().size(), "Exactly one Slack message should be sent");
        
        String sentMessage = mockSlack.getSentMessages().get(0);
        
        // The core assertion for VW-454
        assertTrue(
            sentMessage.contains(expectedGitHubUrl), 
            "Slack body must include the GitHub issue URL. Expected to contain: " + expectedGitHubUrl + " but was: " + sentMessage
        );
    }

    @Test
    void shouldHandleGitHubFailureGracefully_withoutSendingSlack() {
        // Arrange
        ((InMemoryGitHubPort) gitHubPort).setSimulateFailure(true);

        // Act
        defectReportService.reportDefect("Fail Test", "This should not post to Slack");

        // Assert
        InMemorySlackNotificationPort mockSlack = (InMemorySlackNotificationPort) slackPort;
        assertTrue(
            mockSlack.getSentMessages().isEmpty(), 
            "No Slack message should be sent if GitHub issue creation fails."
        );
    }
}

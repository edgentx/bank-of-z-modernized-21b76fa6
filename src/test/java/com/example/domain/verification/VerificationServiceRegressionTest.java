package com.example.domain.verification;

import com.example.domain.verification.model.ReportDefectCommand;
import com.example.domain.verification.service.VerificationService;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for Story S-FB-1: Validating VW-454.
 * Ensures that when a defect is reported, the resulting Slack notification
 * contains the link to the created GitHub issue.
 *
 * Context: The compiler errors in the main code suggest the Port interfaces
 * and implementation were misaligned. These tests define the correct expected
 * behavior (Red Phase) to guide the fix.
 */
class VerificationServiceRegressionTest {

    private MockGitHubPort mockGitHub;
    private MockSlackNotificationPort mockSlack;
    private VerificationService service;

    @BeforeEach
    void setUp() {
        mockGitHub = new MockGitHubPort();
        mockSlack = new MockSlackNotificationPort();
        // Injecting mocks into the service
        service = new VerificationService(mockGitHub, mockSlack);
    }

    @Test
    void reportDefect_shouldCreateIssueOnGitHub() {
        // Arrange
        Map<String, String> metadata = new HashMap<>();
        ReportDefectCommand cmd = new ReportDefectCommand(
                "DEF-454",
                "VW-454: Missing GitHub URL",
                "Slack body does not contain the link",
                "LOW",
                metadata
        );
        String expectedUrl = "https://github.com/mock/issues/454";
        mockGitHub.setMockUrl(expectedUrl);

        // Act
        service.reportDefect(cmd);

        // Assert
        assertEquals("VW-454: Missing GitHub URL", mockGitHub.getLastTitle(), "GitHub title should match command title");
        assertEquals("Slack body does not contain the link", mockGitHub.getLastBody(), "GitHub body should match command description");
        assertNotNull(mockGitHub.getLastLabels(), "Labels should be passed");
        assertEquals("LOW", mockGitHub.getLastLabels().get("severity"), "Severity label should be set");
    }

    @Test
    void reportDefect_shouldPostSlackNotificationContainingGitHubUrl() {
        // Arrange
        String defectTitle = "VW-454 Regression Test";
        String defectDescription = "Validating VW-454 end-to-end";
        ReportDefectCommand cmd = new ReportDefectCommand(
                "S-FB-1",
                defectTitle,
                defectDescription,
                "LOW",
                new HashMap<>()
        );

        String expectedGitHubUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        mockGitHub.setMockUrl(expectedGitHubUrl);

        // Act
        service.reportDefect(cmd);

        // Assert
        // This is the core acceptance criteria for S-FB-1
        String slackMessage = mockSlack.getLastMessage();
        assertNotNull(slackMessage, "Slack message should not be null");

        // The defect report specifically mentions verifying the "Slack body contains GitHub issue link"
        assertTrue(slackMessage.contains(expectedGitHubUrl),
                "Slack message body must contain the GitHub issue URL. Received: " + slackMessage);

        assertTrue(slackMessage.contains(defectTitle), "Slack message should include context (title)");
    }

    @Test
    void reportDefect_shouldTargetCorrectSlackChannel() {
        // Arrange
        ReportDefectCommand cmd = new ReportDefectCommand(
                "ID-1", "T", "D", "HIGH", new HashMap<>()
        );

        // Act
        service.reportDefect(cmd);

        // Assert
        // From defect description: "checking #vforce360-issues"
        assertEquals("vforce360-issues", mockSlack.getLastChannelId(),
                "Notification should be sent to the vforce360-issues channel");
    }

    @Test
    void reportDefect_whenGitHubFails_shouldPropagateException() {
        // Arrange
        mockGitHub.setShouldFail(true);
        ReportDefectCommand cmd = new ReportDefectCommand(
                "ID-2", "T", "D", "LOW", new HashMap<>()
        );

        // Act & Assert
        // We expect the process to fail if GitHub creation fails
        assertThrows(RuntimeException.class, () -> service.reportDefect(cmd),
                "Service should throw if GitHub issue creation fails");

        // And Slack should NOT be called
        assertNull(mockSlack.getLastMessage(), "Slack should not be notified if GitHub fails");
    }
}

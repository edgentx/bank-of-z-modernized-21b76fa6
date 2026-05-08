package com.example.domain.validation;

import com.example.ports.SlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase: Unit tests for defect VW-454.
 *
 * defect: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * Severity: LOW
 * Component: validation
 *
 * Reproduction Steps:
 * 1. Trigger _report_defect via temporal-worker exec
 * 2. Verify Slack body contains GitHub issue link
 *
 * Expected Behavior:
 * Slack body includes GitHub issue: <url>
 *
 * Actual Behavior:
 * (To be verified, historically failing)
 */
class SlackNotificationServiceTest {

    private final SlackNotificationPort slackPort = mock(SlackNotificationPort.class);
    private final GitHubIssuePort githubPort = mock(GitHubIssuePort.class);

    // This class is the System Under Test (SUT) which we will implement in the next phase.
    // For now, we assume it exists and implements the logic.
    private final ValidationService validationService = new ValidationService(slackPort, githubPort);

    @Test
    void whenDefectReported_thenSlackBodyContainsGitHubUrl() {
        // Arrange
        String defectId = "S-FB-1";
        String expectedTitle = "Fix: Validating VW-454";
        String expectedUrl = "https://github.com/example/project/issues/454";

        // Mock the GitHub API to return a valid URL (simulating successful creation/retrieval)
        when(githubPort.createIssue(eq(expectedTitle), anyString()))
            .thenReturn(expectedUrl);

        // Act
        validationService.reportDefect(defectId, expectedTitle, "Component: validation");

        // Assert
        ArgumentCaptor<String> slackBodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(slackPort).sendMessage(slackBodyCaptor.capture());

        String actualBody = slackBodyCaptor.getValue();
        
        // CORE ASSERTION for VW-454: The URL must be present
        assertTrue(actualBody.contains(expectedUrl), 
            "Slack body should contain the GitHub issue URL: " + expectedUrl + " but was: " + actualBody);
        
        // Ensure the link is formatted correctly (Slack standard)
        assertTrue(actualBody.contains("<" + expectedUrl + ">"), 
            "Slack link should be wrapped in <> for auto-linking, but was: " + actualBody);
    }

    @Test
    void whenGitHubCreationFails_thenSlackBodyContainsErrorInfo() {
        // Arrange
        String defectId = "S-FB-1";
        String expectedTitle = "Fix: Validating VW-454";

        // Simulate a failure/empty response from GitHub
        when(githubPort.createIssue(eq(expectedTitle), anyString()))
            .thenReturn(null); // Or throw an exception depending on error handling strategy

        // Act
        validationService.reportDefect(defectId, expectedTitle, "Component: validation");

        // Assert
        ArgumentCaptor<String> slackBodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(slackPort).sendMessage(slackBodyCaptor.capture());
        String actualBody = slackBodyCaptor.getValue();

        // Even if GitHub fails, we expect a valid Slack message, possibly without the link.
        assertNotNull(actualBody);
        assertFalse(actualBody.contains("<"), "Should not contain Slack link formatting if URL is missing");
    }
}

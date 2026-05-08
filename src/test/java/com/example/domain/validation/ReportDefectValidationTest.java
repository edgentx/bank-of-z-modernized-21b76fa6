package com.example.domain.validation;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.example.steps.ReportDefectWorkflow;
import com.example.steps.ReportDefectCmd;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Regression test for defect VW-454.
 *
 * Context: When _report_defect is triggered via temporal-worker,
 * the system must ensure the resulting Slack body contains the GitHub issue link.
 */
@ExtendWith(MockitoExtension.class)
class ReportDefectValidationTest {

    @Mock
    private GitHubIssuePort githubIssuePort;

    @Mock
    private SlackNotificationPort slackNotificationPort;

    @InjectMocks
    private ReportDefectWorkflow workflow; // The System Under Test (SUT)

    // Constants from VW-454 context
    private static final String CHANNEL = "#vforce360-issues";
    private static final String DEFECT_TITLE = "VW-454: GitHub URL in Slack body";
    private static final String EXPECTED_GH_URL = "https://github.com/bank-of-z/vforce360/issues/454";

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenReportingDefect() {
        // Arrange
        ReportDefectCmd command = new ReportDefectCmd(
            "VW-454",
            "GitHub URL missing from Slack notification",
            "LOW",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );

        // We mock the GitHub port to return a valid URL
        when(githubIssuePort.createIssue(anyString(), anyString()))
            .thenReturn(EXPECTED_GH_URL);

        // Act
        workflow.execute(command);

        // Assert
        // 1. Verify GitHub was called
        verify(githubIssuePort).createIssue(
            contains("VW-454"),
            contains("GitHub URL missing from Slack notification")
        );

        // 2. Capture the exact message sent to Slack
        ArgumentCaptor<String> slackBodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(slackNotificationPort).sendNotification(eq(CHANNEL), slackBodyCaptor.capture());

        String actualSlackBody = slackBodyCaptor.getValue();

        // 3. The core validation: The Slack body MUST include the GitHub URL
        // This test FAILS (Red) if the body is null or doesn't contain the link.
        assertThat(actualSlackBody)
            .as("Slack body should contain the GitHub issue URL")
            .isNotNull()
            .contains(EXPECTED_GH_URL);

        // 4. Optional: Verify the specific format requested in VW-454
        // Expected: "GitHub issue: <url>"
        assertThat(actualSlackBody)
            .as("Slack body should format the link as 'GitHub issue: <url>'")
            .contains("GitHub issue: " + EXPECTED_GH_URL);
    }

    @Test
    void shouldFailIfGitHubServiceReturnsNullUrl() {
        // Arrange
        ReportDefectCmd command = new ReportDefectCmd(
            "VW-454", "Missing link", "LOW", "pid"
        );

        when(githubIssuePort.createIssue(anyString(), anyString())).thenReturn(null);

        // Act & Assert
        // The workflow should fail gracefully or explicitly if the URL is missing
        // Assuming the current implementation throws an exception or creates a bad message.
        // If it creates a bad message ("GitHub issue: null"), the above test might catch it,
        // but an explicit check for null is good TDD practice.
        assertThatThrownBy(() -> workflow.execute(command))
            .isNotNull(); // We expect a failure state if URL is null

        // Verify Slack was NOT called if GitHub creation failed (or returned null)
        verify(slackNotificationPort, never()).sendNotification(anyString(), anyString());
    }

    @Test
    void shouldNotSendSlackNotificationIfGitHubCreationFails() {
        // Arrange
        ReportDefectCmd command = new ReportDefectCmd(
            "VW-454", "Test", "LOW", "pid"
        );

        when(githubIssuePort.createIssue(anyString(), anyString()))
            .thenThrow(new RuntimeException("GitHub API Timeout"));

        // Act & Assert
        assertThatThrownBy(() -> workflow.execute(command))
            .isInstanceOf(RuntimeToken.class); // Adjust exception type as needed

        // Ensure we don't send a "Ghost" notification to Slack without the link
        verify(slackNotificationPort, never()).sendNotification(anyString(), anyString());
    }
}

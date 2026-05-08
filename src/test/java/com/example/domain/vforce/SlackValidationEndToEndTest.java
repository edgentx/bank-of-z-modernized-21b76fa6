package com.example.domain.vforce;

import com.example.domain.vforce.port.SlackNotificationPort;
import com.example.domain.vforce.port.GitHubIssuePort;
import com.example.domain.vforce.model.ReportDefectCommand;
import com.example.domain.vforce.model.DefectReportedEvent;
import com.example.domain.vforce.model.GitHubIssue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Regression test for VW-454.
 * Validates that when a defect is reported, the resulting Slack notification
 * body contains the correct GitHub issue URL.
 */
class SlackValidationEndToEndTest {

    /**
     * Given a defect report command
     * When the system processes the report via the Temporal workflow
     * Then the Slack message body must contain the link to the created GitHub issue
     */
    @Test
    void shouldContainGitHubUrlInSlackBody() {
        // Arrange
        String defectSummary = "VW-454: GitHub URL missing";
        String expectedGitHubUrl = "https://github.com/bank-of-z/issues/454";

        // We are mocking the adapters here, but testing the logical glue between them.
        // In a real E2E test with Spring Boot Test, we might use @SpringBootTest,
        // but for pure TDD Red phase, we verify the coordinator logic.
        
        GitHubIssuePort mockGitHubPort = mock(GitHubIssuePort.class);
        SlackNotificationPort mockSlackPort = mock(SlackNotificationPort.class);
        
        GitHubIssue createdIssue = new GitHubIssue("VW-454", expectedGitHubUrl);
        when(mockGitHubPort.createIssue(anyString(), anyString())).thenReturn(createdIssue);

        // This represents the SUT (System Under Test) - the Workflow/Orchestrator
        DefectReportingOrchestrator orchestrator = new DefectReportingOrchestrator(mockGitHubPort, mockSlackPort);

        ReportDefectCommand cmd = new ReportDefectCommand(defectSummary, "Critical", "Details...");

        // Act
        orchestrator.report(cmd);

        // Assert
        verify(mockSlackPort).notify(argThat(message -> {
            // The core validation for VW-454
            return message.body() != null 
                && message.body().contains(expectedGitHubUrl)
                && message.body().contains("<" + expectedGitHubUrl + "|"); // Check for Slack link format
        }));
    }

    /**
     * Edge case: If GitHub fails, we should not send a notification (or send a failure one).
     * Currently validating the happy path as per the story's Expected Behavior.
     */
    @Test
    void shouldNotSendSlackNotificationIfGitHubCreationFails() {
        // Arrange
        GitHubIssuePort mockGitHubPort = mock(GitHubIssuePort.class);
        SlackNotificationPort mockSlackPort = mock(SlackNotificationPort.class);

        when(mockGitHubPort.createIssue(anyString(), anyString())).thenThrow(new RuntimeException("GitHub API Error"));

        DefectReportingOrchestrator orchestrator = new DefectReportingOrchestrator(mockGitHubPort, mockSlackPort);
        ReportDefectCommand cmd = new ReportDefectCommand("Test", "Low", "Test");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orchestrator.report(cmd));
        verify(mockSlackPort, never()).notify(any());
    }
}

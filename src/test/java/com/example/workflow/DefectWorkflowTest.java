package com.example.workflow;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.SlackPort;
import com.example.ports.GitHubPort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase: Workflow tests.
 * Story: S-FB-1
 */
class DefectWorkflowTest {

    @Test
    void should_include_github_url_in_slack_body() {
        // Arrange
        SlackPort mockSlack = mock(SlackPort.class);
        GitHubPort mockGitHub = mock(GitHubPort.class);
        
        // Stubbing GitHub port to return a fake URL
        when(mockGitHub.createIssue(anyString(), anyString(), anyString()))
            .thenReturn("https://github.com/fake-repo/issues/454");

        var workflow = new DefectWorkflowImpl(mockSlack, mockGitHub);
        var cmd = new ReportDefectCmd(
            "Validating VW-454", 
            "Verify Slack body contains GitHub issue link", 
            "LOW", 
            "validation"
        );

        // Act
        workflow.reportDefect(cmd);

        // Assert
        var slackCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSlack).sendNotification(slackCaptor.capture());
        
        String slackBody = slackCaptor.getValue();
        
        // The core acceptance criteria: Slack body includes GitHub issue: <url>
        assertTrue(slackBody.contains("GitHub issue:"), "Slack body should contain 'GitHub issue:' label");
        assertTrue(slackBody.contains("https://github.com/fake-repo/issues/454"), "Slack body should contain the actual GitHub URL");
    }

    @Test
    void should_call_github_with_correct_parameters() {
        // Arrange
        SlackPort mockSlack = mock(SlackPort.class);
        GitHubPort mockGitHub = mock(GitHubPort.class);

        when(mockGitHub.createIssue(anyString(), anyString(), anyString()))
            .thenReturn("http://url");

        var workflow = new DefectWorkflowImpl(mockSlack, mockGitHub);
        var cmd = new ReportDefectCmd("Summary", "Description", "LOW", "validation");

        // Act
        workflow.reportDefect(cmd);

        // Assert
        verify(mockGitHub).createIssue("Summary", "Description", "bug");
    }
}
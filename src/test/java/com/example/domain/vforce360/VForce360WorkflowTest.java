package com.example.domain.vforce360;

import com.example.application.DefectReportWorkflowOrchestrator;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.example.workers.ReportDefectActivity;
import com.example.workers.TemporalWorker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase for Story S-FB-1.
 * Tests the end-to-end flow of reporting a defect via Temporal,
 * verifying that the Slack notification contains the GitHub issue URL.
 */
class VForce360WorkflowTest {

    private GitHubIssuePort mockGitHub;
    private SlackNotificationPort mockSlack;
    private ReportDefectActivity activity;
    private DefectReportWorkflowOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        mockGitHub = mock(GitHubIssuePort.class);
        mockSlack = mock(SlackNotificationPort.class);
        
        // Inject mocks into the activity implementation
        activity = new ReportDefectActivity(mockGitHub, mockSlack);
        
        // Wire up the orchestrator
        orchestrator = new DefectReportWorkflowOrchestrator(activity);
    }

    @Test
    void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        String defectTitle = "VW-454 — GitHub URL in Slack body";
        String defectDescription = "Verification steps...";
        String expectedGitHubUrl = "https://github.com/example/bank-of-z/issues/454";
        String slackChannel = "#vforce360-issues";

        // Mock GitHub response
        when(mockGitHub.createIssue(anyString(), anyString())).thenReturn(expectedGitHubUrl);
        // Mock Slack success
        when(mockSlack.postMessage(eq(slackChannel), anyString())).thenReturn(true);

        // Act
        orchestrator.reportDefect(defectTitle, defectDescription, slackChannel);

        // Assert
        // 1. Verify GitHub was called
        ArgumentCaptor<String> githubTitleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> githubDescCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockGitHub).createIssue(githubTitleCaptor.capture(), githubDescCaptor.capture());
        assertEquals(defectTitle, githubTitleCaptor.getValue());
        assertEquals(defectDescription, githubDescCaptor.getValue());

        // 2. Verify Slack was called
        ArgumentCaptor<String> slackBodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSlack).postMessage(eq(slackChannel), slackBodyCaptor.capture());
        
        // 3. **Critical Assertion**: The Slack body must contain the GitHub URL
        String actualSlackBody = slackBodyCaptor.getValue();
        assertTrue(
            actualSlackBody.contains(expectedGitHubUrl),
            "Slack body should contain the GitHub issue URL. Got: " + actualSlackBody
        );
    }

    @Test
    void testReportDefect_GitHubFailure_ShouldPropagateError() {
        // Arrange
        when(mockGitHub.createIssue(anyString(), anyString()))
            .thenThrow(new RuntimeException("GitHub API Connection Refused"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orchestrator.reportDefect("Fail", "Desc", "#channel");
        });

        // Verify Slack was not called if GitHub failed
        verify(mockSlack, never()).postMessage(anyString(), anyString());
    }
}
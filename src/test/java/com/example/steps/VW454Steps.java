package com.example.steps;

import com.example.domain.shared.ReportDefectCmd;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.example.validation.ValidationWorkflow;
import com.example.validation.ValidationWorkflowImpl;
import io.temporal.workflow.Workflow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase Tests for Story S-FB-1 (VW-454).
 * Verifies that Slack notifications include GitHub links.
 */
public class VW454Steps {

    private SlackNotificationPort mockSlack;
    private GitHubIssuePort mockGitHub;
    private ValidationWorkflow workflow;

    @BeforeEach
    public void setUp() {
        mockSlack = mock(SlackNotificationPort.class);
        mockGitHub = mock(GitHubIssuePort.class);
        workflow = new ValidationWorkflowImpl(mockSlack, mockGitHub);
    }

    @Test
    public void testReportDefect_generatesSlackBodyWithGitHubLink() {
        // GIVEN
        String defectId = "VW-454";
        String expectedGithubUrl = "https://github.com/example/issues/454";
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, "Validation Error", "Body content missing", "LOW");

        // Mock the GitHub port to return a URL
        when(mockGitHub.getIssueUrl(defectId)).thenReturn(expectedGithubUrl);

        // WHEN
        workflow.reportDefect(cmd);

        // THEN
        ArgumentCaptor<String> slackBodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSlack, times(1)).sendMessage(slackBodyCaptor.capture());

        String actualSlackBody = slackBodyCaptor.getValue();

        // FAILING ASSERTIONS (Red Phase)
        // AC: Slack body includes GitHub issue: <url>
        assertTrue(actualSlackBody.contains(expectedGithubUrl), 
            "Slack body should contain the exact GitHub URL: " + expectedGithubUrl + " but was: " + actualSlackBody);
        
        // AC: The validation no longer exhibits the reported behavior (Link missing)
        assertFalse(actualSlackBody.isEmpty(), "Slack body should not be empty");
    }
}

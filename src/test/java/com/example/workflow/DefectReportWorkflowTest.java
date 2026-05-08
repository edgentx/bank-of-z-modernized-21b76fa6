package com.example.workflow;

import com.example.domain.reporting.model.ReportDefectCmd;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.example.ports.dto.IssueRequest;
import com.example.ports.dto.IssueResponse;
import com.example.ports.dto.SlackMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * End-to-End style unit test for the Defect Report Workflow.
 * Validates that the GitHub URL is passed to the Slack body.
 */
class DefectReportWorkflowTest {

    @Test
    void givenValidDefectReport_whenExecuted_thenSlackBodyContainsGitHubUrl() {
        // 1. Setup Mocks
        GitHubIssuePort mockGitHubPort = mock(GitHubIssuePort.class);
        SlackNotificationPort mockSlackPort = mock(SlackNotificationPort.class);

        // Define GitHub behavior
        String expectedUrl = "https://github.com/bank-of-z/vforce360/issues/VW-454";
        IssueResponse githubResponse = new IssueResponse(expectedUrl, "VW-454");
        
        when(mockGitHubPort.createIssue(any(IssueRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(githubResponse));

        // Define Slack behavior - capture the argument
        // Since we can't easily verify argument matches string without custom matchers in some contexts,
        // we use a simple Answer to capture the message object.
        final SlackMessage[] capturedMessage = new SlackMessage[1];
        doAnswer(invocation -> {
            capturedMessage[0] = invocation.getArgument(0);
            return null;
        }).when(mockSlackPort).sendNotification(any(SlackMessage.class));

        // 2. Execute Workflow (Simulating Temporal Activity)
        ReportDefectCmd cmd = new ReportDefectCmd("VW-454", "Validation failed", "LOW", Instant.now());
        
        DefectReportWorkflow workflow = new DefectReportWorkflow(mockGitHubPort, mockSlackPort);
        
        // Run synchronously for test
        workflow.reportDefect(cmd).join();

        // 3. Verify
        // 3a. GitHub was called
        verify(mockGitHubPort, times(1)).createIssue(any(IssueRequest.class));
        
        // 3b. Slack was called
        verify(mockSlackPort, times(1)).sendNotification(any(SlackMessage.class));
        
        // 3c. CRITICAL: Verify URL is in Slack Body
        assertNotNull(capturedMessage[0], "Slack message should have been sent");
        String slackBody = capturedMessage[0].body();
        
        assertTrue(slackBody.contains(expectedUrl), 
            "Slack body must contain the GitHub issue URL. Expected: " + expectedUrl + " in: " + slackBody);
    }

    @Test
    void givenGitHubUrlIsNull_whenExecuted_thenValidationFailsOrSlackHasPlaceholder() {
         // Setup Mocks returning empty/null (simulating defect VW-454 scenario)
        GitHubIssuePort mockGitHubPort = mock(GitHubIssuePort.class);
        SlackNotificationPort mockSlackPort = mock(SlackNotificationPort.class);

        IssueResponse failedResponse = new IssueResponse("", ""); // Empty URL
        when(mockGitHubPort.createIssue(any(IssueRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(failedResponse));

        final SlackMessage[] capturedMessage = new SlackMessage[1];
        doAnswer(invocation -> {
            capturedMessage[0] = invocation.getArgument(0);
            return null;
        }).when(mockSlackPort).sendNotification(any(SlackMessage.class));

        ReportDefectCmd cmd = new ReportDefectCmd("VW-454", "Validation failed", "LOW", Instant.now());
        DefectReportWorkflow workflow = new DefectReportWorkflow(mockGitHubPort, mockSlackPort);

        // Act
        workflow.reportDefect(cmd).join();

        // Assert - If URL is empty, Slack body should probably indicate failure or have placeholder
        // This test documents the "Actual Behavior" mentioned in the story
        String slackBody = capturedMessage[0].body();
        assertFalse(slackBody.contains("http"), "URL should be missing in this defect scenario");
    }
}

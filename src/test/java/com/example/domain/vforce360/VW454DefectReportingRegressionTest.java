package com.example.domain.vforce360;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import com.example.domain.vforce360.model.ReportDefectCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * E2E Regression Test for Story S-FB-1 (VW-454).
 * 
 * Context: Validate that when a defect report is executed (via Temporal or direct trigger),
 * the resulting Slack notification body contains the correct GitHub issue URL.
 * 
 * Reproduction Steps:
 * 1. Trigger _report_defect via temporal-worker exec (simulated by command execution here).
 * 2. Verify Slack body contains GitHub issue link.
 * 
 * Expected Behavior: Slack body includes GitHub issue: <url>.
 */
@ExtendWith(MockitoExtension.class)
class VW454DefectReportingRegressionTest {

    @Mock
    private GitHubPort gitHubPort;

    @Mock
    private SlackNotificationPort slackNotificationPort;

    /**
     * Test Case: Verify GitHub URL is present in Slack notification for a valid defect ID.
     */
    @Test
    void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // ARRANGE
        String defectId = "VW-454";
        String expectedGitHubUrl = "https://github.com/example/bank-of-z/issues/454";
        
        // Configure mocks
        when(gitHubPort.getIssueUrl(defectId)).thenReturn(Optional.of(expectedGitHubUrl));
        when(slackNotificationPort.sendNotification(any())).thenReturn(true);

        // Initialize the handler/workflow unit under test
        // In a real Temporal test, this might be a WorkflowStub. Here we test the logic directly.
        DefectReportingWorkflow workflow = new DefectReportingWorkflow(gitHubPort, slackNotificationPort);

        // ACT
        // Trigger the defect report
        workflow.execute(new ReportDefectCommand(defectId, "User reported validation issue."));

        // ASSERT
        // 1. Verify GitHub was queried
        verify(gitHubPort).getIssueUrl(defectId);

        // 2. Verify Slack was called
        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(slackNotificationPort).sendNotification(payloadCaptor.capture());

        // 3. Verify the content (The Fix for VW-454)
        Map<String, Object> capturedPayload = payloadCaptor.getValue();
        assertNotNull(capturedPayload.get("text"), "Slack body 'text' should not be null");
        
        String slackBody = (String) capturedPayload.get("text");
        
        // The core assertion: The URL must be in the body.
        assertTrue(
            slackBody.contains(expectedGitHubUrl), 
            "Slack body should contain the GitHub issue URL: " + expectedGitHubUrl + " but was: " + slackBody
        );
    }

    /**
     * Test Case: Verify graceful handling if GitHub URL is not found.
     * The notification should still be sent, but perhaps without the link or with a warning.
     */
    @Test
    void testReportDefect_WhenGitHubUrlMissing_ShouldStillSendSlackNotification() {
        // ARRANGE
        String defectId = "VW-UNKNOWN";
        
        when(gitHubPort.getIssueUrl(defectId)).thenReturn(Optional.empty());
        when(slackNotificationPort.sendNotification(any())).thenReturn(true);

        DefectReportingWorkflow workflow = new DefectReportingWorkflow(gitHubPort, slackNotificationPort);

        // ACT
        workflow.execute(new ReportDefectCommand(defectId, "Unknown issue."));

        // ASSERT
        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(slackNotificationPort).sendNotification(payloadCaptor.capture());
        
        Map<String, Object> capturedPayload = payloadCaptor.getValue();
        assertNotNull(capturedPayload.get("text"));
        // We just verify it doesn't crash and sends something.
    }
}
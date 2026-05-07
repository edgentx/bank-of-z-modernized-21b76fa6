package com.example.workflow;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.infrastructure.github.GitHubIssueService;
import com.example.infrastructure.slack.SlackNotificationService;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * E2E Regression Test for VW-454.
 * Verifies the complete flow:
 * 1. Report Defect
 * 2. Create GitHub Issue
 * 3. Send Slack Notification
 * 4. Validate Slack Body contains the Link.
 */
class ReportDefectWorkflowTest {

    private GitHubPort mockGitHubPort;
    private SlackPort mockSlackPort;
    private GitHubIssueService gitHubService;
    private SlackNotificationService slackService;
    private ReportDefectWorkflow workflow;

    @BeforeEach
    void setUp() {
        mockGitHubPort = mock(GitHubPort.class);
        mockSlackPort = mock(SlackPort.class);
        
        // Initialize real adapters with mocked ports
        gitHubService = new GitHubIssueService(mockGitHubPort, new com.example.infrastructure.config.GitHubProperties());
        slackService = new SlackNotificationService(mockSlackPort, new com.example.infrastructure.config.GitHubProperties());
        
        workflow = new ReportDefectWorkflow(gitHubService, slackService);
    }

    @Test
    void execute_generatesGitHubUrlAndSendsInSlackBody() {
        // 1. Trigger _report_defect via temporal-worker exec (Simulated)
        String defectId = "VW-454";
        String title = "GitHub URL in Slack body (end-to-end)";
        String description = "Severity: LOW";
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, title, description);

        // Mock GitHub Response
        String expectedUrl = "https://github.com/bank-of-z/issues/" + defectId;
        when(mockGitHubPort.createIssue(title, description)).thenReturn(expectedUrl);

        // 2. Execute Workflow
        workflow.execute(cmd);

        // Verify GitHub was called
        verify(mockGitHubPort).createIssue(title, description);

        // 3. Verify Slack body contains GitHub issue link
        ArgumentCaptor<String> slackBodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSlackPort).sendMessage(slackBodyCaptor.capture());

        String actualSlackBody = slackBodyCaptor.getValue();

        // Expected Behavior: Slack body includes GitHub issue: <url>
        assertTrue(actualSlackBody.contains(expectedUrl), 
            "Regression Test Failed: Slack body did not contain the expected GitHub URL. Actual: " + actualSlackBody);
    }
}

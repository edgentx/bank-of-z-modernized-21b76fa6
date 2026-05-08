package com.example.steps;

import com.example.ports.GithubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * E2E Regression Test for S-FB-1.
 * Verifies that when a defect is reported, the resulting Slack notification
 * contains the URL to the created GitHub issue.
 */
@SpringBootTest(classes = SFB1TestConfig.class)
public class SFB1E2ETest {

    @MockBean
    private GithubIssuePort githubIssuePort;

    @MockBean
    private SlackNotificationPort slackNotificationPort;

    @Autowired
    private ReportDefectWorkflow workflow; // System under test

    @Test
    public void testReportDefect_ShouldIncludeGithubUrlInSlackBody() {
        // 1. Setup: Define expected behavior
        String defectTitle = "VW-454: GitHub URL missing";
        String expectedGithubUrl = "https://github.com/example/repo/issues/454";

        // Mock GitHub to return a valid URL when createIssue is called
        when(githubIssuePort.createIssue(anyString(), anyString()))
            .thenReturn(expectedGithubUrl);

        // Mock Slack to return success
        when(slackNotificationPort.postMessage(anyString())).thenReturn(true);

        // 2. Execute: Trigger the workflow (Temporal activity simulation)
        workflow.reportDefect(defectTitle);

        // 3. Verify: Check GitHub creation was attempted
        verify(githubIssuePort).createIssue(contains(defectTitle), anyString());

        // 4. Verify: Check Slack message body contains the specific GitHub URL
        // This is the core assertion for S-FB-1 (VW-454)
        verify(slackNotificationPort).postMessage(contains(expectedGithubUrl));
    }
}
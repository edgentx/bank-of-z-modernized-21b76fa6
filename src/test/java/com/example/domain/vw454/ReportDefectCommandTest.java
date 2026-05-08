package com.example.domain.vw454;

import com.example.domain.shared.UnknownCommandException;
import com.example.ports.SlackNotifier;
import com.example.ports.GitHubIssueTracker;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase: S-FB-1
 * Testing that the ReportDefect use case triggers Slack notifications
 * that successfully include the GitHub issue URL.
 */
class ReportDefectCommandTest {

    private final MockGitHubIssueTracker github = new MockGitHubIssueTracker();
    private final MockSlackNotifier slack = new MockSlackNotifier();
    private final ReportDefectCommandHandler handler = new ReportDefectCommandHandler(github, slack);

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenDefectIsReported() {
        // Arrange: Simulating temporal-worker exec triggering a defect report
        String defectId = "VW-454";
        String defectSummary = "Validating VW-454 — GitHub URL in Slack body (end-to-end)";
        String repoUrl = "https://github.com/example/bank-of-z";

        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            defectSummary,
            repoUrl,
            "LOW"
        );

        // Act: Execute the command
        handler.handle(cmd);

        // Assert: Verify Slack body contains the issue link
        // Expected: Slack body includes GitHub issue: <url>
        // We verify the mock captured the state correctly.
        
        assertTrue(slack.wasNotifyCalled(), "Slack notification should have been triggered");
        
        String actualSlackBody = slack.getLastBody();
        assertNotNull(actualSlackBody, "Slack body should not be null");
        
        // The defect report implies the URL was missing or incorrectly formatted.
        // We assert the URL is present and formatted as a link.
        assertTrue(actualSlackBody.contains("GitHub issue:"), "Body should contain 'GitHub issue:' label");
        
        URI expectedUri = github.getCreatedIssueUrl();
        assertNotNull(expectedUri, "GitHub should have returned a valid Issue URL");
        
        // The critical validation: the slack body must contain the specific URL returned by GitHub
        assertTrue(actualSlackBody.contains(expectedUri.toString()), 
            "Slack body must include the actual GitHub issue URL returned by the API");
    }
}

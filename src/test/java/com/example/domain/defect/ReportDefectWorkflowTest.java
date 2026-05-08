package com.example.domain.defect;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.example.mocks.InMemoryGitHubIssuePort;
import com.example.mocks.SpySlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Regression Test for S-FB-1: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * 
 * Tests the _report_defect workflow to ensure that when a GitHub issue is successfully created,
 * the subsequent Slack notification contains the link to that issue in the message body.
 */
class ReportDefectWorkflowTest {

    private SpySlackNotificationPort slackPort;
    private InMemoryGitHubIssuePort githubPort;
    private ReportDefectWorkflow workflow;

    @BeforeEach
    void setUp() {
        slackPort = new SpySlackNotificationPort();
        githubPort = new InMemoryGitHubIssuePort();
        workflow = new ReportDefectWorkflow(slackPort, githubPort);
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenIssueIsCreated() {
        // Arrange
        String expectedUrl = "https://github.com/example/bank-of-z/issues/454";
        githubPort.setNextIssueUrl(expectedUrl);

        String defectTitle = "VW-454: Defect found in validation";
        String repo = "example/bank-of-z";

        // Act
        workflow.reportDefect(defectTitle, repo);

        // Assert
        // 1. Verify GitHub issue creation was attempted
        assertThat(githubPort.getCreateIssueCallCount()).isGreaterThan(0);

        // 2. Verify Slack was called
        assertThat(slackPort.wasCalled()).isTrue();
        
        // 3. CRITICAL ASSERTION: Verify the body contains the GitHub URL
        String actualBody = slackPort.getLastBody();
        assertThat(actualBody)
            .as("Slack body must contain the GitHub issue URL")
            .contains(expectedUrl);
    }

    @Test
    void shouldNotIncludeGitHubUrlIfIssueCreationFails() {
        // Arrange
        // Simulate GitHub API failure by returning empty optional
        githubPort.setNextIssueUrl(null); 

        String defectTitle = "VW-454: Defect found in validation";
        String repo = "example/bank-of-z";

        // Act
        workflow.reportDefect(defectTitle, repo);

        // Assert
        assertThat(slackPort.wasCalled()).isTrue();
        
        // The body should contain an error message or placeholder, but NOT a valid URL
        // This ensures we don't send a malformed link if GitHub is down
        String actualBody = slackPort.getLastBody();
        assertThat(actualBody)
            .as("Slack body should indicate GitHub failure")
            .contains("Failed to create GitHub issue");
            
        // Verify it doesn't look like a URL
        assertThat(actualBody).doesNotContain("https://github.com");
    }

    @Test
    void shouldHandleEmptyUrlGracefully() {
        // Arrange
        githubPort.setNextIssueUrl(""); // Empty string but not null
        String defectTitle = "Defect";

        // Act
        workflow.reportDefect(defectTitle, "repo");

        // Assert
        String actualBody = slackPort.getLastBody();
        // Should either be empty URL or error, but not crash
        assertThat(actualBody).isNotNull();
    }
}

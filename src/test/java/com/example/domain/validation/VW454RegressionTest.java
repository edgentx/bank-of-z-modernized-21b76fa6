package com.example.domain.validation;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.port.SlackNotifier;
import com.example.domain.validation.port.GitHubIssueTracker;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase: Regression test for VW-454.
 * Verifies that the GitHub URL is correctly injected into the Slack notification body.
 */
class VW454RegressionTest {

    private final GitHubIssueTracker mockGitHub = mock(GitHubIssueTracker.class);
    private final SlackNotifier mockSlack = mock(SlackNotifier.class);

    @Test
    void shouldIncludeGitHubUrlInSlackBody() {
        // Given: A defect command with a valid payload
        String correlationId = "vw-454-test-case";
        String summary = "Validation logic failure";
        String description = "The URL is missing from the Slack message";
        
        ReportDefectCmd cmd = new ReportDefectCmd(correlationId, summary, description);
        ValidationAggregate aggregate = new ValidationAggregate(correlationId, mockGitHub, mockSlack);

        // When: The command is executed, simulating a successful GitHub creation
        // We mock the external GitHub API to return a valid URL
        String expectedUrl = "https://github.com/bank-of-z/issues/454";
        when(mockGitHub.createIssue(summary, description))
            .thenReturn(expectedUrl);

        // Execute command
        aggregate.execute(cmd);

        // Then: The Slack notifier must have been called
        verify(mockSlack).notify(argThat(payload -> {
            // CRITICAL ASSERTION: The body must contain the specific GitHub URL
            return payload != null 
                && payload.contains("GitHub issue") 
                && payload.contains(expectedUrl);
        }));
    }

    @Test
    void shouldFailIfGitHubUrlMissingInSlackPayload() {
        // Given
        String correlationId = "vw-454-negative-case";
        ReportDefectCmd cmd = new ReportDefectCmd(correlationId, "Test", "Desc");
        ValidationAggregate aggregate = new ValidationAggregate(correlationId, mockGitHub, mockSlack);

        when(mockGitHub.createIssue(anyString(), anyString()))
            .thenReturn("https://github.com/bank-of-z/issues/123");

        // When
        aggregate.execute(cmd);

        // Then
        verify(mockSlack).notify(argThat(payload -> {
            if (payload == null) return false;
            // If this assertion fails, the defect VW-454 is reproduced.
            boolean hasUrl = payload.contains("https://github.com/bank-of-z/issues/123");
            if (!hasUrl) {
                throw new AssertionError("VW-454 Reproduced: Slack body does not contain GitHub URL. Body was: " + payload);
            }
            return true;
        }));
    }
}

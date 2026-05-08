package com.example.domain.validation;

import com.example.ports.SlackNotifier;
import com.example.ports.GitHubIssueTracker;
import com.example.domain.validation.model.ReportDefectCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase: Verify that the defect report process results in a Slack notification
 * containing the correct GitHub issue URL.
 *
 * Story: S-FB-1
 * Defect: VW-454
 */
@ExtendWith(MockitoExtension.class)
class ReportDefectCommandHandlerTest {

    @Mock
    private GitHubIssueTracker mockGitHub;

    @Mock
    private SlackNotifier mockSlack;

    @InjectMocks
    private ReportDefectCommandHandler handler;

    @Test
    void handle_ShouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        String expectedTitle = "VW-454: GitHub URL in Slack body validation";
        String expectedUrl = "https://github.com/bank-of-z/project/issues/454";

        // Configure the mock GitHub adapter to return a specific URL when an issue is created
        when(mockGitHub.createIssue(any(ReportDefectCommand.class)))
            .thenReturn(expectedUrl);

        ReportDefectCommand cmd = new ReportDefectCommand(
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1",
            expectedTitle,
            "Severity: LOW\nComponent: validation\n\nReproduction Steps..."
        );

        // Act
        handler.handle(cmd);

        // Assert
        // Verify that the Slack adapter was called exactly once
        verify(mockSlack, times(1)).notify(argThat(payload -> {
            // This is the critical assertion for S-FB-1.
            // The Slack body must contain the URL returned by the GitHub adapter.
            String body = payload.body();
            return body != null && body.contains(expectedUrl);
        }));

        // Verify that the GitHub adapter was actually called (to get the URL in the first place)
        verify(mockGitHub, times(1)).createIssue(argThat(c -> 
            c.title().equals(expectedTitle)
        ));
    }

    @Test
    void handle_ShouldThrowExceptionIfGitHubUrlIsMissing() {
        // This test validates the contract: if GitHub fails or returns null, we shouldn't notify Slack with an empty link
        // (Or alternatively, the handler should propagate the error).
        // For this defect fix, we ensure we don't send a "null" link.

        when(mockGitHub.createIssue(any())).thenReturn(null);

        ReportDefectCommand cmd = new ReportDefectCommand(
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1",
            "Test",
            "Test"
        );

        assertThrows(IllegalStateException.class, () -> {
            handler.handle(cmd);
        });

        // Verify Slack was never touched if GitHub failed
        verify(mockSlack, never()).notify(any());
    }
}

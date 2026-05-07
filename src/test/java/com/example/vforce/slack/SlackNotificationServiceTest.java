package com.example.vforce.slack;

import com.example.ports.SlackPort;
import com.example.vforce.github.GithubIssue;
import com.example.vforce.github.GithubPort;
import com.example.vforce.shared.ReportDefectCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TDD Red Phase for S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 *
 * Testing the SlackNotificationService to ensure that when a defect is reported,
 * and a GitHub issue is successfully created, the resulting GitHub URL is
 * strictly present in the body of the Slack message.
 */
@ExtendWith(MockitoExtension.class)
class SlackNotificationServiceTest {

    @Mock
    private GithubPort githubPort;

    @Mock
    private SlackPort slackPort;

    private SlackNotificationService service;

    @BeforeEach
    void setUp() {
        // Assuming constructor injection for the mock ports
        service = new SlackNotificationService(slackPort, githubPort);
    }

    @Test
    void handleReportDefect_shouldIncludeGitHubUrlInSlackMessage_whenIssueIsCreated() {
        // Given
        String defectSummary = "System fails to validate IDs";
        String expectedUrl = "https://github.com/bank-of-z/issues/454";
        ReportDefectCommand cmd = new ReportDefectCommand(defectSummary);

        // Mock GitHub creation success
        GithubIssue createdIssue = new GithubIssue(expectedUrl);
        when(githubPort.createIssue(any())).thenReturn(Optional.of(createdIssue));

        // When
        service.handleReportDefect(cmd);

        // Then
        // We verify the SlackPort was called, but more importantly, we capture the payload
        // to assert the content. This is the 'Red Phase' failing assertion if implementation is missing.
        verify(slackPort).sendMessage(payload -> {
            assertThat(payload).contains("GitHub issue: " + expectedUrl);
            // This assertion ensures the URL is not just present, but formatted as requested.
            return true; // Mockito ArgumentMatcher verification
        });
    }

    @Test
    void handleReportDefect_shouldStillSendSlackMessage_butWithoutUrl_whenGitHubCreationFails() {
        // Given
        String defectSummary = "Connectivity timeout";
        ReportDefectCommand cmd = new ReportDefectCommand(defectSummary);

        // Mock GitHub creation failure (returns empty)
        when(githubPort.createIssue(any())).thenReturn(Optional.empty());

        // When
        service.handleReportDefect(cmd);

        // Then
        verify(slackPort).sendMessage(payload -> {
            assertThat(payload).contains("Defect Reported: " + defectSummary);
            assertThat(payload).doesNotContain("GitHub issue:");
            return true;
        });
    }

    @Test
    void handleReportDefect_shouldThrowException_whenBothServicesFail() {
        // Given
        ReportDefectCommand cmd = new ReportDefectCommand("Critical failure");

        when(githubPort.createIssue(any())).thenReturn(Optional.empty());
        when(slackPort.sendMessage(any())).thenThrow(new RuntimeException("Slack API Down"));

        // Then/When
        assertThatThrownBy(() -> service.handleReportDefect(cmd))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Slack API Down");
    }
}

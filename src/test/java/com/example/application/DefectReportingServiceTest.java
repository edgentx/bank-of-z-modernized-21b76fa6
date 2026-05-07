package com.example.application;

import com.example.domain.vforce.model.DefectAggregate;
import com.example.domain.vforce.model.DefectReportedEvent;
import com.example.domain.vforce.model.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for DefectReportingService.
 * Validates that the GitHub URL flows from GitHub -> Event -> Slack.
 */
class DefectReportingServiceTest {

    private GitHubPort gitHubPort;
    private SlackPort slackPort;
    private DefectReportingService service;

    @BeforeEach
    void setUp() {
        gitHubPort = mock(GitHubPort.class);
        slackPort = mock(SlackPort.class);
        service = new DefectReportingService(gitHubPort, slackPort);
    }

    @Test
    void testReportDefect_EndToEnd_FlowIncludesGitHubUrl() {
        // Arrange
        String summary = "VW-454: GitHub URL missing in Slack";
        String description = "Fix the integration between GitHub and Slack.";
        String expectedGitHubUrl = "https://github.com/mock/issues/123";

        when(gitHubPort.createIssue(eq(summary), eq(description))).thenReturn(expectedGitHubUrl);

        // Act
        DefectReportedEvent result = service.reportDefect(new ReportDefectCmd(summary, description));

        // Assert
        assertNotNull(result);
        assertEquals("DefectReported", result.type());
        assertNotNull(result.aggregateId());
        assertEquals(expectedGitHubUrl, result.githubIssueUrl());
        assertNotNull(result.occurredAt());

        // Verify GitHub was called
        verify(gitHubPort, times(1)).createIssue(summary, description);

        // Verify Slack was called with the URL
        ArgumentCaptor<String> slackUrlCaptor = ArgumentCaptor.forClass(String.class);
        verify(slackPort, times(1)).notifyDefectReported(eq(summary), slackUrlCaptor.capture());
        assertEquals(expectedGitHubUrl, slackUrlCaptor.getValue());
    }

    @Test
    void testReportDefect_GitHubFailure_HandledGracefully() {
        // Arrange
        String summary = "Critical Bug";
        String description = "System down";
        
        when(gitHubPort.createIssue(any(), any())).thenThrow(new RuntimeException("GitHub API Timeout"));

        // Act
        DefectReportedEvent result = service.reportDefect(new ReportDefectCmd(summary, description));

        // Assert
        assertNotNull(result);
        assertTrue(result.githubIssueUrl().contains("ERROR"));
        
        // Verify Slack was still called
        verify(slackPort, times(1)).notifyDefectReported(eq(summary), anyString());
    }
}

package com.example.application;

import com.example.application.DefectReportingService;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for DefectReportingService.
 * Verifies that when a defect is reported, a GitHub issue is created
 * and the Slack notification includes the URL to that issue.
 *
 * Corresponds to Story S-FB-1.
 */
@ExtendWith(MockitoExtension.class)
class DefectReportingServiceTest {

    @Mock
    private GitHubPort gitHubPort;

    @Mock
    private SlackNotificationPort slackPort;

    private DefectReportingService service;

    private static final String GITHUB_URL = "https://github.com/example/repo/issues/123";

    @BeforeEach
    void setUp() {
        service = new DefectReportingService(gitHubPort, slackPort);
    }

    @Test
    void handleDefectReportedEvent_shouldCreateGitHubIssue() {
        // Arrange
        DefectReportedEvent event = new DefectReportedEvent(
                "project-123",
                "Test Defect",
                "This is a description",
                "Tester",
                "LOW"
        );
        when(gitHubPort.createIssue(anyString(), anyString(), anyMap())).thenReturn(GITHUB_URL);

        // Act
        service.handleDefectReportedEvent(event);

        // Assert
        verify(gitHubPort).createIssue(
                eq("Test Defect"),
                contains("This is a description"),
                anyMap()
        );
    }

    @Test
    void handleDefectReportedEvent_shouldPostToSlackWithGitHubUrl() {
        // Arrange
        DefectReportedEvent event = new DefectReportedEvent(
                "project-123",
                "VW-454 Validation Error",
                "Slack body missing GitHub URL",
                "User",
                "LOW"
        );

        when(gitHubPort.createIssue(anyString(), anyString(), anyMap())).thenReturn(GITHUB_URL);

        // Act
        service.handleDefectReportedEvent(event);

        // Assert
        ArgumentCaptor<String> slackMessageCaptor = ArgumentCaptor.forClass(String.class);
        verify(slackPort).postMessage(slackMessageCaptor.capture());

        String sentMessage = slackMessageCaptor.getValue();

        // Verify the message contains the specific GitHub URL returned by the mock
        assertTrue(sentMessage.contains(GITHUB_URL), "Slack message should contain the GitHub issue URL: " + GITHUB_URL);
        
        // Verify it contains the project context
        assertTrue(sentMessage.contains("project-123"), "Slack message should reference the project ID");
    }

    @Test
    void handleDefectReportedEvent_shouldFailGracefullyIfGitHubFails() {
        // Arrange
        DefectReportedEvent event = new DefectReportedEvent(
                "project-123",
                "Critical Bug",
                "System crash",
                "Admin",
                "HIGH"
        );
        when(gitHubPort.createIssue(anyString(), anyString(), anyMap())).thenThrow(new RuntimeException("GitHub API Timeout"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.handleDefectReportedEvent(event));
        
        // Verify we didn't post to Slack if GitHub failed
        verify(slackPort, never()).postMessage(anyString());
    }
}
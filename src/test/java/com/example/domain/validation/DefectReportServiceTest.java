package com.example.domain.validation;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefectReportServiceTest {

    @Mock
    private GitHubPort gitHubPort;

    @Mock
    private SlackNotificationPort slackPort;

    private DefectReportService service;

    @BeforeEach
    void setUp() {
        service = new DefectReportService(gitHubPort, slackPort);
    }

    @Test
    void reportDefect_shouldPublishToSlack_withGitHubLink_whenIssueCreationSucceeds() {
        // Arrange
        String defectSummary = "Critical Bug in Login";
        String expectedUrl = "https://github.com/mock-repo/issues/454";
        
        when(gitHubPort.createIssue(anyString(), anyString())).thenReturn(Optional.of(expectedUrl));
        when(slackPort.postMessage(anyString(), anyString())).thenReturn(true);

        // Act
        service.reportDefect(defectSummary, "Detailed description...");

        // Assert
        // Expected Behavior: Slack body includes GitHub issue: <url>
        verify(slackPort).postMessage(endsWith("-issues"), endsWith(expectedUrl));
    }

    @Test
    void reportDefect_shouldPublishToSlack_withErrorMessage_whenIssueCreationFails() {
        // Arrange
        String defectSummary = "Low Severity UI Glitch";
        when(gitHubPort.createIssue(anyString(), anyString())).thenReturn(Optional.empty());
        when(slackPort.postMessage(anyString(), anyString())).thenReturn(true);

        // Act
        service.reportDefect(defectSummary, "Description");

        // Assert
        // If GitHub fails, we still expect a Slack message with a failure indicator
        verify(slackPort).postMessage(endsWith("-issues"), endsWith("Failed to create GitHub issue"));
    }
}

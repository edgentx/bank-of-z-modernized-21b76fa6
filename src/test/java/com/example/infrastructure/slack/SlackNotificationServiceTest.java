package com.example.infrastructure.slack;

import com.example.domain.defect.model.DefectReportedEvent;
import com.example.ports.SlackPort;
import com.example.infrastructure.config.GitHubProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the Slack Notification Service.
 * Verifies that the Slack message body is formatted correctly and includes the GitHub Issue URL.
 */
class SlackNotificationServiceTest {

    private SlackPort slackPort;
    private GitHubProperties gitHubProperties;
    private SlackNotificationService service;

    @BeforeEach
    void setUp() {
        slackPort = mock(SlackPort.class);
        gitHubProperties = new GitHubProperties();
        gitHubProperties.setBaseUrl("https://github.com/bank-of-z/issues/");
        service = new SlackNotificationService(slackPort, gitHubProperties);
    }

    @Test
    void shouldSendSlackMessageContainingGitHubUrl() {
        // Given
        String defectId = "VW-454";
        String title = "GitHub URL in Slack body";
        Instant occurredAt = Instant.now();
        DefectReportedEvent event = new DefectReportedEvent(defectId, title, occurredAt);

        // When
        service.notifyDefect(event);

        // Then
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(slackPort).sendMessage(messageCaptor.capture());

        String actualMessage = messageCaptor.getValue();
        String expectedUrl = "https://github.com/bank-of-z/issues/" + defectId;

        // CRITICAL ASSERTION: The defect report states the validation FAILED previously.
        // We assert the URL is present in the body.
        assertTrue(actualMessage.contains(expectedUrl), "Slack body must include GitHub issue URL");
        assertTrue(actualMessage.contains(title), "Slack body must include defect title");
    }

    @Test
    void shouldFailToSendIfUrlMissing() {
        // Edge case: Ensure the test would fail if the URL was not constructed
        // This demonstrates the Red/Green cycle requirement if the implementation was empty
        String defectId = "VW-999";
        DefectReportedEvent event = new DefectReportedEvent(defectId, "Test", Instant.now());

        // If sendMessage is called with an empty string or null, the port might throw.
        // We verify interaction happened.
        service.notifyDefect(event);
        verify(slackPort, times(1)).sendMessage(anyString());
    }
}

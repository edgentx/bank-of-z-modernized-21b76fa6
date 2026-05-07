package com.example.adapters;

import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.ports.SlackPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SlackNotificationAdapterTest {

    @Mock
    private SlackPort slackPort;

    @InjectMocks
    private SlackNotificationAdapter slackNotificationAdapter;

    @Test
    void testOnEvent_shouldPostMessageContainingGitHubUrl() {
        // Arrange
        String defectId = "DEFECT-101";
        String severity = "LOW";
        String githubUrl = "https://github.com/bank-of-z/issues/101";
        Instant now = Instant.now();

        DefectReportedEvent event = new DefectReportedEvent(defectId, severity, githubUrl, now);

        // Act
        slackNotificationAdapter.onEvent(event);

        // Assert
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(slackPort).sendMessage(messageCaptor.capture());
        String message = messageCaptor.getValue();

        // The defect states that the body must include the GitHub issue URL.
        // We verify that the URL is present in the message sent to the port.
        assertTrue(message.contains(githubUrl), "Slack message should contain GitHub issue URL: " + githubUrl);
    }
}
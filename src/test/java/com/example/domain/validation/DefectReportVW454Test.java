package com.example.domain.validation;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.SlackNotificationPostedEvent;
import com.example.domain.validation.port.SlackPort;
import com.example.domain.validation.port.GitHubPort;
import com.example.domain.shared.UnknownCommandException;
import com.example.mocks.InMemoryValidationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Regression Test for VW-454.
 * Ensures that when a defect is reported, the resulting Slack notification
 * contains the clickable URL to the GitHub issue that was created.
 */
class DefectReportVW454Test {

    private InMemoryValidationRepository repository;
    private ValidationAggregate aggregate;
    private SlackPort mockSlack;
    private GitHubPort mockGitHub;

    @BeforeEach
    void setUp() {
        repository = new InMemoryValidationRepository();
        mockSlack = mock(SlackPort.class);
        mockGitHub = mock(GitHubPort.class);
        
        // Setup standard mock responses for happy path
        when(mockGitHub.createIssue(anyString(), anyString(), anyString()))
            .thenReturn("https://github.com/bank-of-z/issues/454");
    }

    @Test
    void slackBody_shouldContainGitHubIssueUrl() {
        // Arrange
        String defectId = "VW-454";
        String description = "Validating VW-454 — GitHub URL in Slack body (end-to-end)";
        String severity = "LOW";
        String component = "validation";
        
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, description, severity, component);
        
        aggregate = new ValidationAggregate(defectId, mockSlack, mockGitHub);

        // Act
        List events = aggregate.execute(cmd);

        // Assert
        assertEquals(1, events.size(), "Should produce a single event");
        assertTrue(events.get(0) instanceof SlackNotificationPostedEvent, "Event should be SlackNotificationPostedEvent");

        // Verify the interaction with the Slack port to ensure the URL is passed through
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSlack, times(1)).postMessage(messageCaptor.capture());

        String actualSlackMessage = messageCaptor.getValue();
        
        // Critical assertion for VW-454: The URL must be in the message body
        assertNotNull(actualSlackMessage, "Slack message body should not be null");
        assertTrue(actualSlackMessage.contains("https://github.com/bank-of-z/issues/454"), 
            "Slack body must include the GitHub issue URL: " + actualSlackMessage);
        
        // Ensure the link is formatted as a clickable URL (basic check)
        assertTrue(actualSlackMessage.contains("<https://") || actualSlackMessage.contains("http://"), 
            "URL should be formatted as a link in Slack");
    }

    @Test
    void shouldFailOnInvalidCommand() {
        // Arrange
        String defectId = "VW-454";
        aggregate = new ValidationAggregate(defectId, mockSlack, mockGitHub);
        Object badCmd = new Object();

        // Act & Assert
        assertThrows(UnknownCommandException.class, () -> aggregate.execute((com.example.domain.shared.Command) badCmd));
    }
}

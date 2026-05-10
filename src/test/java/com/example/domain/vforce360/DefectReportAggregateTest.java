package com.example.domain.vforce360;

import com.example.domain.shared.Command;
import com.example.domain.shared.UnknownCommandException;
import com.example.ports.SlackNotificationPort;
import com.example.ports.GitHubIntegrationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase Test for S-FB-1 (VW-454).
 * Validates that triggering a defect report results in a Slack body
 * containing the correctly formatted GitHub issue URL.
 */
class DefectReportAggregateTest {

    // We use mocks for external dependencies to satisfy the 'Mock Adapter Pattern'
    private SlackNotificationPort mockSlack;
    private GitHubIntegrationPort mockGitHub;
    private DefectReportAggregate aggregate;

    @BeforeEach
    void setUp() {
        mockSlack = mock(SlackNotificationPort.class);
        mockGitHub = mock(GitHubIntegrationPort.class);
        // We construct the aggregate with its dependencies
        aggregate = new DefectReportAggregate("agg-1", mockSlack, mockGitHub);
    }

    @Test
    void shouldThrowExceptionWhenCommandUnknown() {
        // Arrange
        Command unknownCmd = new Command() {};

        // Act & Assert
        assertThrows(UnknownCommandException.class, () -> aggregate.execute(unknownCmd));
    }

    @Test
    void shouldFailValidationIfTitleIsBlank() {
        // Arrange
        ReportDefectCommand cmd = new ReportDefectCommand(
            "agg-1",
            "   ", // Blank title
            "LOW",
            "validation",
            "Steps to reproduce"
        );

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("title required"));
    }

    @Test
    void shouldFailValidationIfSeverityIsInvalid() {
        // Arrange
        ReportDefectCommand cmd = new ReportDefectCommand(
            "agg-1",
            "Valid Title",
            "ULTRA_HIGH", // Invalid severity
            "validation",
            "Steps"
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldPublishSlackMessageContainingGitHubUrl() {
        // Arrange
        String expectedTitle = "Validating VW-454";
        String expectedUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        ReportDefectCommand cmd = new ReportDefectCommand(
            "agg-1",
            expectedTitle,
            "LOW",
            "validation",
            "Reproduction Steps..."
        );

        // Mock the GitHub adapter to return a valid URL
        when(mockGitHub.createIssue(anyString(), anyString(), anyString())).thenReturn(expectedUrl);

        // Act
        aggregate.execute(cmd);

        // Assert
        // 1. Verify GitHub port was called
        verify(mockGitHub).createIssue(contains(expectedTitle), contains("validation"), anyString());

        // 2. Verify Slack port was called
        ArgumentCaptor<String> slackBodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSlack).sendNotification(slackBodyCaptor.capture());

        String actualSlackBody = slackBodyCaptor.getValue();
        
        // 3. CRITICAL ASSERTION: Slack body includes the GitHub issue URL
        assertTrue(actualSlackBody.contains(expectedUrl), 
            "Slack body should contain the GitHub issue URL, but was: " + actualSlackBody);
    }

    @Test
    @SuppressWarnings("unchecked")
    void storeEventVersion1() {
         // Arrange
        ReportDefectCommand cmd = new ReportDefectCommand(
            "agg-1",
            "VW-100",
            "LOW",
            "logic",
            "..."
        );
        when(mockGitHub.createIssue(anyString(), anyString(), anyString())).thenReturn("http://url");

        // Act
        List events = aggregate.execute(cmd);

        // Assert
        assertFalse(events.isEmpty());
        assertEquals(1, aggregate.getVersion());
    }
}

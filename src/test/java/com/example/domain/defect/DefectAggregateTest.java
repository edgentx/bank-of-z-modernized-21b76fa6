package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCommand;
import com.example.domain.shared.UnknownCommandException;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TDD Red Phase Tests for Story S-FB-1.
 * 
 * Tests cover:
 * 1. Successful defect reporting flow (GitHub created -> Slack notified with URL).
 * 2. Validation failure scenarios (null inputs).
 * 3. External dependency wiring (using mock ports).
 */
class DefectAggregateTest {

    private final GitHubPort mockGitHub = Mockito.mock(GitHubPort.class);
    private final SlackNotificationPort mockSlack = Mockito.mock(SlackNotificationPort.class);

    @Test
    void testReportDefectSuccess_ShouldContainGitHubUrlInSlackBody() {
        // Arrange
        String defectId = "DEF-123";
        String title = "Fix Validating VW-454";
        String description = "GitHub URL in Slack body (end-to-end)";
        String severity = "LOW";
        String expectedUrl = "https://github.com/tech/bank-of-z/issues/454";

        when(mockGitHub.createIssue(title, description)).thenReturn(Optional.of(expectedUrl));

        DefectAggregate aggregate = new DefectAggregate(defectId, mockGitHub, mockSlack);
        ReportDefectCommand cmd = new ReportDefectCommand(defectId, title, description, severity);

        // Act
        aggregate.execute(cmd);

        // Assert
        // Expected Behavior: Slack body includes GitHub issue: <url>
        verify(mockSlack).sendNotification(
            Mockito.contains("GitHub issue: " + expectedUrl)
        );
        verify(mockGitHub).createIssue(title, description);
    }

    @Test
    void testReportDefectFailure_GitHubUnavailable() {
        // Arrange
        String defectId = "DEF-404";
        String title = "Critical Bug";
        String description = "System down";
        String severity = "HIGH";

        when(mockGitHub.createIssue(anyString(), anyString())).thenReturn(Optional.empty());

        DefectAggregate aggregate = new DefectAggregate(defectId, mockGitHub, mockSlack);
        ReportDefectCommand cmd = new ReportDefectCommand(defectId, title, description, severity);

        // Act
        // We expect the system to handle the empty Optional gracefully,
        // possibly notifying Slack of a reporting failure.
        aggregate.execute(cmd);

        // Assert
        verify(mockGitHub).createIssue(title, description);
        verify(mockSlack).sendNotification(
            Mockito.contains("Failed to create GitHub issue")
        );
    }

    @Test
    void testValidation_TitleRequired() {
        // Arrange
        String defectId = "DEF-001";
        ReportDefectCommand cmd = new ReportDefectCommand(defectId, null, "desc", "LOW");
        DefectAggregate aggregate = new DefectAggregate(defectId, mockGitHub, mockSlack);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("title required"));
    }

    @Test
    void testValidation_SeverityRequired() {
        // Arrange
        String defectId = "DEF-002";
        ReportDefectCommand cmd = new ReportDefectCommand(defectId, "Title", "desc", null);
        DefectAggregate aggregate = new DefectAggregate(defectId, mockGitHub, mockSlack);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("severity required"));
    }
}

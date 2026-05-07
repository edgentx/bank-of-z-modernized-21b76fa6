package com.example.vforce;

import com.example.application.DefectReportingActivities;
import com.example.application.DefectReportingActivitiesImpl;
import com.example.domain.shared.validation.ValidationViolation;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.NotificationPort;
import com.example.vforce.shared.ReportDefectCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for S-FB-1.
 * 
 * Scenario:
 * 1. Trigger _report_defect (via Activities)
 * 2. Verify GitHub issue is created
 * 3. Verify Slack body contains the GitHub URL
 * 
 * Tech Stack: JUnit 5 (Mockito/Spring not strictly needed for pure Java unit tests, but JUnit is the runner).
 */
class DefectReportingE2ETest {

    private GitHubPort githubPort;
    private NotificationPort slackPort;
    private DefectReportingActivities activity;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        githubPort = new MockGitHubPort("https://github.com/mock-org/repo/issues/454");
        slackPort = new MockNotificationPort();

        // Inject dependencies into the concrete implementation
        // Note: In the real Spring app, @ActivityImpl would handle this wiring.
        activity = new DefectReportingActivitiesImpl(githubPort, slackPort);
    }

    @Test
    void testReportDefect_endToEnd_success() {
        // Arrange
        List<ValidationViolation> violations = List.of(
            new ValidationViolation("amount", "must be positive"),
            new ValidationViolation("currency", "invalid ISO code")
        );
        
        ReportDefectCommand command = new ReportDefectCommand(
            "VW-454: Validation Error",
            "System detected invalid input during transfer.",
            violations
        );

        // Act
        activity.reportDefect(command);

        // Assert: GitHub Port was called
        MockGitHubPort mockGitHub = (MockGitHubPort) githubPort;
        assertEquals(1, mockGitHub.getCreateCallCount(), "GitHub issue should be created once");

        // Assert: Slack Port was called
        MockNotificationPort mockSlack = (MockNotificationPort) slackPort;
        assertTrue(mockSlack.wasNotified(), "Slack should receive a notification");

        // CRITICAL ASSERTION FOR S-FB-1
        // The defect states: "Verify Slack body contains GitHub issue link"
        // Since we are mocking the ports, we verify the Data passed to the Slack port contains the Link info.
        ReportDefectCommand slackMessage = mockSlack.getLastNotification();
        
        // The implementation (which we are forcing into existence via TDD) 
        // is expected to enrich the description or add a field with the URL.
        // We verify the URL exists in the final description payload sent to Slack.
        String expectedUrl = "https://github.com/mock-org/repo/issues/454";
        
        assertTrue(
            slackMessage.description().contains(expectedUrl),
            "S-FB-1 REGRESSION: Slack body description must contain the GitHub URL. Found: " + slackMessage.description()
        );
    }

    @Test
    void testReportDefect_generatesMarkdownLink() {
        // Arrange
        String title = "Critical Failure in DB2 Connector";
        ReportDefectCommand command = new ReportDefectCommand(title, "DB2 connection timed out", List.of());
        String expectedUrl = "https://github.com/mock-org/repo/issues/999";
        
        // Update mock for this specific test
        githubPort = new MockGitHubPort(expectedUrl);
        activity = new DefectReportingActivitiesImpl(githubPort, slackPort);

        // Act
        activity.reportDefect(command);

        // Assert
        ReportDefectCommand result = ((MockNotificationPort) slackPort).getLastNotification();
        
        // Verify the link is formatted (optional but good for "Slack body")
        assertTrue(
            result.description().contains(expectedUrl),
            "Expected raw URL in description"
        );
    }
}
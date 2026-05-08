package com.example.e2e.regression;

import com.example.domain.shared.DefectReportedEvent;
import com.example.domain.shared.ReportDefectCommand;
import com.example.domain.validation.ValidationAggregate;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * S-FB-1: Regression test for VW-454.
 * Verifies that when a defect is reported via the Validation Aggregate,
 * the resulting Slack notification body contains the GitHub Issue URL.
 */
public class SFB1ValidationWorkflowTest {

    private MockGitHubPort mockGitHub;
    private MockSlackPort mockSlack;
    private ValidationAggregate aggregate;

    private static final String DEFECT_ID = "VW-454";
    private static final String EXPECTED_GITHUB_URL = "https://github.com/example/repo/issues/454";

    @BeforeEach
    void setUp() {
        mockGitHub = new MockGitHubPort();
        mockSlack = new MockSlackPort();
        
        // Configure Mock GitHub to return a specific URL so we can verify it's passed to Slack
        mockGitHub.setSimulatedUrl(EXPECTED_GITHUB_URL);
        
        // Instantiate the aggregate with mocks
        aggregate = new ValidationAggregate(DEFECT_ID, mockGitHub, mockSlack);
    }

    @Test
    @DisplayName("Given a defect report command, when executed, then Slack body must contain GitHub URL")
    void testSlackBodyContainsGitHubUrl() {
        // Arrange
        ReportDefectCommand cmd = new ReportDefectCommand(
            DEFECT_ID,
            "GitHub URL missing in Slack body",
            "The defect report needs to link the issue.",
            "LOW"
        );

        // Act
        aggregate.execute(cmd);

        // Assert
        // 1. Verify the mock received the message
        String lastSlackMessage = mockSlack.getLastMessage();
        assertNotNull(lastSlackMessage, "Slack should have been notified");

        // 2. Verify the content (VW-454 Check)
        // The test fails if the URL is NOT present.
        assertTrue(
            lastSlackMessage.contains(EXPECTED_GITHUB_URL),
            "Slack body must contain the GitHub Issue URL: " + EXPECTED_GITHUB_URL + ". " +
            "Actual body was: " + lastSlackMessage
        );
    }

    @Test
    @DisplayName("Given a defect report, when GitHub creation fails, then no Slack message is sent")
    void testNoSlackNotificationOnGitHubFailure() {
        // Arrange
        mockGitHub.setShouldFail(true);
        ReportDefectCommand cmd = new ReportDefectCommand(
            DEFECT_ID, "Fail Test", "Test failure propagation", "LOW"
        );

        // Act & Assert
        // We expect an exception because the aggregate handles GitHub failure as a terminal state for the report attempt
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("Failed to create GitHub issue"));
        assertNull(mockSlack.getLastMessage(), "Slack should NOT be notified if GitHub issue creation failed");
    }

    @Test
    @DisplayName("Given a defect report, when executed, then DefectReportedEvent contains URL")
    void testEventStateContainsUrl() {
        // Arrange
        ReportDefectCommand cmd = new ReportDefectCommand(
            DEFECT_ID, "Event Test", "Check event payload", "LOW"
        );

        // Act
        var events = aggregate.execute(cmd);

        // Assert
        assertEquals(1, events.size(), "Should emit one event");
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        
        assertEquals(EXPECTED_GITHUB_URL, event.issueUrl(), "Event must contain the generated GitHub URL");
    }
}

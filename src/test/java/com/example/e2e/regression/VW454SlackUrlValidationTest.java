package com.example.e2e.regression;

import com.example.application.ReportDefectCommand;
import com.example.application.ReportDefectUseCase;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * VW-454 Regression Test.
 * Validates that the Slack body includes the GitHub issue link
 * when a defect is reported via the temporal worker.
 *
 * Corresponds to Story S-FB-1.
 */
class VW454SlackUrlValidationTest {

    private MockSlackNotificationPort mockSlack;
    private ReportDefectUseCase useCase;

    @BeforeEach
    void setUp() {
        // We inject the mock port into the Use Case simulating Spring Dependency Injection
        mockSlack = new MockSlackNotificationPort();
        useCase = new ReportDefectUseCase(mockSlack);
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBody_whenReportingDefect() {
        // Arrange
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String expectedGithubUrl = "https://github.com/bank-of-z/issues/454";
        ReportDefectCommand cmd = new ReportDefectCommand(
            projectId,
            "VW-454",
            "GitHub URL in Slack body",
            expectedGithubUrl,
            "LOW"
        );

        // Act
        useCase.execute(cmd);

        // Assert
        // 1. Verify a message was sent to #vforce360-issues
        assertEquals(1, mockSlack.getMessages().size(), "Slack notification should be sent once");

        var sentMessage = mockSlack.getMessages().get(0);
        assertEquals("#vforce360-issues", sentMessage.channel, "Message should be routed to #vforce360-issues");

        // 2. Verify the body contains the specific URL (VW-454 requirement)
        assertTrue(
            sentMessage.body.contains(expectedGithubUrl),
            "Slack body must contain the GitHub issue URL: " + expectedGithubUrl + "\nActual Body: " + sentMessage.body
        );

        // 3. Verify general format (sanity check)
        assertTrue(sentMessage.body.contains("Defect Reported"), "Body should indicate a defect report");
    }

    @Test
    void shouldFailGracefully_ifUrlIsMissing() {
        // Arrange
        ReportDefectCommand cmd = new ReportDefectCommand(
            "p-123",
            "VW-999",
            "No URL provided",
            null,
            "LOW"
        );

        // Act
        useCase.execute(cmd);

        // Assert
        var sentMessage = mockSlack.getMessages().get(0);
        // If URL is missing, we expect a placeholder or null handling, not a crash
        assertFalse(sentMessage.body.contains("http"), "Should not contain http if url was null");
        assertTrue(sentMessage.body.contains("URL: N/A"), "Should display N/A for missing URL");
    }
}

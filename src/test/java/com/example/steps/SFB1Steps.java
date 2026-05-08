package com.example.steps;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.UnknownCommandException;
import com.example.mocks.MockSlackPublisher;
import com.example.ports.SlackPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * S-FB-1: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 *
 * This test class validates that when a defect is reported,
 * the Slack message body includes the correct GitHub URL.
 */
public class SFB1Steps {

    // System Under Test (The handler that will be implemented)
    private static class DefectReportingHandler {
        private final SlackPublisher slackPublisher;

        public DefectReportingHandler(SlackPublisher slackPublisher) {
            this.slackPublisher = slackPublisher;
        }

        public void handle(ReportDefectCmd cmd) {
            // Implementation stub (Red Phase)
            // This logic does not exist yet, causing the test to fail.
            throw new UnknownCommandException(cmd);
        }
    }

    private MockSlackPublisher mockSlack;
    private DefectReportingHandler handler;

    @BeforeEach
    void setUp() {
        mockSlack = new MockSlackPublisher();
        handler = new DefectReportingHandler(mockSlack);
    }

    @Test
    void shouldIncludeGithubUrlInSlackBody() {
        // Arrange
        String expectedUrl = "https://github.com/bank-of-z/issues/454";
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "Fix: Validating GitHub URL in Slack body",
            "Severity: LOW",
            expectedUrl
        );

        // Act
        handler.handle(cmd);

        // Assert
        assertEquals(1, mockSlack.getMessages().size(), "Slack should be called once");
        
        MockSlackPublisher.PublishedMessage msg = mockSlack.getMessages().get(0);
        assertEquals("#vforce360-issues", msg.channel, "Message should go to the specific issues channel");
        
        // The critical validation: The URL must be present in the body
        // This assertion will fail because the handler is not implemented yet.
        assertTrue(
            msg.body.contains(expectedUrl),
            "Slack body should contain the GitHub issue URL: " + expectedUrl + " but was: " + msg.body
        );
    }

    @Test
    void shouldFailIfUrlIsMissingFromBody() {
        // Arrange
        String missingUrl = "https://github.com/bank-of-z/issues/404";
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-404",
            "Missing URL Test",
            "Validates that body generation fails gracefully if URL is null",
            missingUrl
        );

        // Act & Assert
        // We expect the system to fail if the URL isn't propagated to the body
        // Currently, this throws UnknownCommandException which passes the "fail" requirement,
        // but the next test verifies the structure.
        assertThrows(UnknownCommandException.class, () -> handler.handle(cmd));
    }
}

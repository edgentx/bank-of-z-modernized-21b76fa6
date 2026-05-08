package com.example.domain.vforce;

import com.example.mocks.MockSlackNotifier;
import com.example.ports.SlackNotifierPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for VW-454.
 * 
 * Story S-FB-1: Verify GitHub URL presence in Slack body during defect reporting.
 * 
 * This test covers the end-to-end scenario where a defect is reported via
 * the temporal-worker execution, and we must validate that the resulting
 * Slack notification contains the correct GitHub issue link.
 */
class VW454ValidationTest {

    // System Under Test
    private final DefectReportService service;
    
    // Mocks
    private final MockSlackNotifier slackNotifier = new MockSlackNotifier();

    public VW454ValidationTest() {
        // Wire mock adapter to the service
        this.service = new DefectReportService(slackNotifier);
    }

    @Test
    void whenReportDefect_thenSlackBodyContainsGitHubUrl() {
        // Arrange
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/example/bank-of-z/issues/454";

        // Act
        service.reportDefect(defectId, expectedUrl);

        // Assert
        // 1. Verify message was sent to the correct channel
        assertFalse(slackNotifier.messages.isEmpty(), "Slack should have received a message");
        MockSlackNotifier.Message msg = slackNotifier.messages.get(0);
        assertEquals("#vforce360-issues", msg.channel(), "Message should be routed to vforce360-issues");

        // 2. Verify the body contains the link (Validation Fix)
        assertTrue(
            msg.body().contains(expectedUrl),
            "Slack body should contain the GitHub URL: " + expectedUrl + "\nActual body: " + msg.body()
        );
        
        // 3. Verify format matches expected pattern (e.g., <url> or raw url)
        // Assuming raw url based on defect description "Slack body includes GitHub issue: <url>"
        assertTrue(
            msg.body().contains("GitHub issue:"),
            "Slack body should contain the context text 'GitHub issue:'"
        );
    }
}

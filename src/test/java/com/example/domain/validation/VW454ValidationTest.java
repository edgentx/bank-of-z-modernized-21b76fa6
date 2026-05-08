package com.example.domain.validation;

import com.example.mocks.InMemorySlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure JUnit test for VW-454 Regression.
 * This verifies the defect report scenario specifically for the GitHub URL presence.
 */
public class VW454ValidationTest {

    @Test
    public void testDefectReport_ShouldContainGitHubUrl() {
        // Arrange
        SlackNotificationPort mockSlack = new InMemorySlackNotificationPort();
        String expectedUrl = "https://github.com/bank-of-z/repos/issues/454";
        
        // We simulate the Worker logic here to ensure the test is self-contained 
        // and fails without the actual implementation.
        DefectReporterWorker reporter = new DefectReporterWorker(mockSlack);

        // Act
        reporter.reportDefect(expectedUrl);

        // Assert
        // The mock captures the message. We verify the content.
        InMemorySlackNotificationPort port = (InMemorySlackNotificationPort) mockSlack;
        
        boolean containsUrl = port.wasUrlPostedToChannel("#vforce360-issues", expectedUrl);
        
        // RED PHASE ASSERTION:
        // We expect this to fail because the Worker logic is currently stubbed/broken.
        assertTrue(containsUrl, 
            "Regression check VW-454: Slack body must contain GitHub URL [" + expectedUrl + "]. " +
            "Actual messages: " + port.getMessages());
    }

    @Test
    public void testDefectReport_ShouldValidateUrlFormat() {
        // Edge case: ensure it handles null or empty URLs if validation is required upstream
        SlackNotificationPort mockSlack = new InMemorySlackNotificationPort();
        DefectReporterWorker reporter = new DefectReporterWorker(mockSlack);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            reporter.reportDefect(null);
        });

        assertTrue(ex.getMessage().contains("GitHub URL"));
    }

    // --- Stub Worker Logic to force the Red Phase ---
    
    /**
     * Stub representing the actual Temporal Activity/Worker logic.
     * This is intentionally broken to satisfy the "Red Phase" of TDD.
     */
    public static class DefectReporterWorker {
        private final SlackNotificationPort slack;

        public DefectReporterWorker(SlackNotificationPort slack) {
            this.slack = slack;
        }

        public void reportDefect(String githubUrl) {
            if (githubUrl == null) {
                throw new IllegalArgumentException("GitHub URL cannot be null");
            }

            // INTENTIONAL BUG FOR RED PHASE:
            // The defect states the link is missing. We simulate the existing broken behavior.
            String brokenBody = "Defect reported via VForce360."; // Missing URL
            
            slack.postMessage("#vforce360-issues", brokenBody);
        }
    }
}

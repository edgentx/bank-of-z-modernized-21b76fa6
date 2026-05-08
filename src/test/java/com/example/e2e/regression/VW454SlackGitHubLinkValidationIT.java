package com.example.e2e.regression;

import com.example.domain.shared.Command;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for Defect VW-454.
 * Validates that when a defect is reported (simulated via Command execution),
 * the resulting Slack notification body contains the valid GitHub issue URL.
 * 
 * Corresponds to Story S-FB-1.
 */
class VW454SlackGitHubLinkValidationIT {

    // System Under Test (SUT) - In a real Spring app this would be an @Autowired Workflow/Service
    // For this structural test, we assume a handler class exists or will be created.
    private DefectReportHandler handler;

    // Mock Adapters (External Dependencies)
    private final MockSlackNotificationPort slackPort = new MockSlackNotificationPort();
    private final MockGitHubIssuePort gitHubPort = new MockGitHubIssuePort();

    @BeforeEach
    void setUp() {
        // Reset mocks
        slackPort.clear();
        gitHubPort.mockUrl(null);

        // Instantiate the handler with mocks
        // This class (DefectReportHandler) represents the implementation 
        // that must be written to make these tests pass.
        handler = new DefectReportHandler(slackPort, gitHubPort);
    }

    @Test
    @DisplayName("VW-454: Slack body should contain GitHub issue URL when defect is reported")
    void testSlackBodyContainsGitHubUrl() {
        // Arrange
        String expectedGitHubUrl = "https://github.com/bank-of-z/legacy-modernization/issues/454";
        String defectId = "VW-454";
        String slackChannel = "C04P5JJPP0P";

        // Configure the Mock GitHub port to return a valid URL
        gitHubPort.mockUrl(expectedGitHubUrl);

        // Act: Trigger the report defect workflow
        // We simulate the temporal-worker exec passing a command to our domain handler
        ReportDefectCommand cmd = new ReportDefectCommand(defectId, slackChannel);
        handler.handle(cmd);

        // Assert: Verify Slack body
        assertEquals(1, slackPort.getMessages().size(), "Slack should have received 1 message");
        
        MockSlackNotificationPort.Message sentMessage = slackPort.getMessages().get(0);
        assertEquals(slackChannel, sentMessage.channelId(), "Message should target the correct channel");
        
        // CRITICAL ASSERTION: The body must contain the link
        String actualBody = sentMessage.body();
        assertTrue(
            actualBody.contains(expectedGitHubUrl), 
            "Slack body must contain the full GitHub Issue URL. Expected to contain: [" + expectedGitHubUrl + "] but got: [" + actualBody + "]"
        );
    }

    @Test
    @DisplayName("VW-454: Slack body should be informative even if GitHub URL is missing (Resilience)")
    void testResilienceIfGitHubUrlMissing() {
        // Arrange
        String defectId = "VW-454";
        String slackChannel = "C04P5JJPP0P";
        
        // Mock returns empty (GitHub service is down or issue not found)
        // MockGitHubIssuePort defaults to Optional.empty() if not set

        // Act
        ReportDefectCommand cmd = new ReportDefectCommand(defectId, slackChannel);
        handler.handle(cmd);

        // Assert
        assertEquals(1, slackPort.getMessages().size(), "Slack should still receive a message");
        String body = slackPort.getMessages().get(0).body();
        
        assertNotNull(body, "Body should not be null");
        // We just ensure it doesn't crash. The content might say "URL unavailable".
    }

    // --- Inner Classes representing the 'glue' that needs to be implemented ---

    /**
     * Command representing the trigger from Temporal.
     */
    public static class ReportDefectCommand implements Command {
        private final String defectId;
        private final String targetChannel;

        public ReportDefectCommand(String defectId, String targetChannel) {
            this.defectId = defectId;
            this.targetChannel = targetChannel;
        }

        public String defectId() { return defectId; }
        public String targetChannel() { return targetChannel; }
    }

    /**
     * This is the class the developer must implement to satisfy the test.
     * It acts as the Workflow/Activity implementation bridge.
     */
    public static class DefectReportHandler {
        private final SlackNotificationPort slackPort;
        private final GitHubIssuePort gitHubPort;

        public DefectReportHandler(SlackNotificationPort slackPort, GitHubIssuePort gitHubPort) {
            this.slackPort = slackPort;
            this.gitHubPort = gitHubPort;
        }

        public void handle(ReportDefectCommand cmd) {
            // Implementation will go here: fetch URL, compose string, send to slack
            // For now, empty method ensures tests FAIL (Red Phase)
            throw new UnsupportedOperationException("Implementation missing to satisfy VW-454");
        }
    }
}

package com.example.domain.validation;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for VW-454.
 * Validates that the Slack body generated during _report_defect execution
 * contains the expected GitHub issue URL.
 */
class VW454SlackLinkValidationTest {

    // We are simulating the handler/orchestrator logic here to test the behavior.
    // In a full implementation, this would be a Spring component.
    private MockSlackNotificationPort slackPort;
    private DefectReportHandler handler;

    @BeforeEach
    void setUp() {
        slackPort = new MockSlackNotificationPort();
        handler = new DefectReportHandler(slackPort);
    }

    @Test
    @DisplayName("S-FB-1: Verify Slack body contains GitHub issue URL")
    void testSlackBodyContainsGitHubUrl() {
        // Arrange
        String expectedDefectId = "VW-454";
        String githubBaseUrl = "https://github.com/bank-of-z/vforce360/issues/";
        ReportDefectCmd cmd = new ReportDefectCmd(
            expectedDefectId,
            "Validation: GitHub URL in Slack body",
            "Description of the defect..."
        );

        // Act
        // Trigger the defect report logic which would normally be called by Temporal
        handler.reportDefect(cmd);

        // Assert
        String actualSlackBody = slackPort.getLastSentMessageBody();
        
        // The test should fail (Red Phase) if the implementation is empty or malformed.
        // We expect the body to contain a valid URL structure.
        assertNotNull(actualSlackBody, "Slack body should not be null");
        
        // Check for the specific URL pattern
        // Expected format example: "Issue created: https://github.com/bank-of-z/vforce360/issues/VW-454"
        assertTrue(
            actualSlackBody.contains(githubBaseUrl + expectedDefectId),
            "Slack body should contain the full GitHub issue URL. Got: " + actualSlackBody
        );

        // Ensure it looks like a URL
        assertTrue(
            actualSlackBody.contains("http"),
            "Slack body should contain a valid protocol (http/https). Got: " + actualSlackBody
        );
    }

    @Test
    @DisplayName("S-FB-1: Regression test for null URL handling")
    void testSlackBodyIsNotNullOrEmpty() {
        // Arrange
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-455",
            "Another Defect",
            "Desc"
        );

        // Act
        handler.reportDefect(cmd);

        // Assert
        String body = slackPort.getLastSentMessageBody();
        assertFalse(body.isEmpty(), "Slack body must not be empty when defect is reported");
        assertNotNull(body, "Slack body must not be null");
    }

    /**
     * Inner class representing the System Under Test (SUT) logic.
     * This class simulates the workflow logic that would process the command
     * and invoke the Slack port.
     */
    private static class DefectReportHandler {
        private final SlackNotificationPort slackPort;

        public DefectReportHandler(SlackNotificationPort slackPort) {
            this.slackPort = slackPort;
        }

        public void reportDefect(ReportDefectCmd cmd) {
            // NOTE: This is a stub implementation to allow the code to compile and the test structure to exist.
            // The test assertions will FAIL against this stub until the real logic is implemented.
            // TODO: Implement GitHub client and URL generation logic.
            
            // Current Stub Logic (Red Phase Placeholder):
            String stubMessage = "Defect reported: " + cmd.defectId();
            
            // If the developer implements the fix, they will change the line below to:
            // String url = gitHubClient.createIssue(cmd);
            // String message = "Issue created: " + url;
            // slackPort.sendMessage(message);
            
            slackPort.sendMessage(stubMessage); 
        }
    }
}

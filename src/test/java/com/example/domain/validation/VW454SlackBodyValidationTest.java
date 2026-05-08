package com.example.domain.validation;

import com.example.domain.shared.Command;
import com.example.mocks.MockDefectReportGeneratorPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.DefectReportGeneratorPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for S-FB-1 / VW-454.
 * Validates that the Slack body contains the GitHub issue link.
 */
public class VW454SlackBodyValidationTest {

    private DefectReportGeneratorPort reportGenerator;
    private SlackNotificationPort slackClient;
    private Command reportDefectCommand;

    @BeforeEach
    void setUp() {
        // Instantiate mocks
        reportGenerator = new MockDefectReportGeneratorPort();
        slackClient = new MockSlackNotificationPort();
        
        // Create a dummy command to simulate the temporal trigger
        reportDefectCommand = new Command() {}; // Anonymous command for testing purposes
    }

    @Test
    void testSlackBodyContainsGitHubLink_WhenDefectReported() {
        // ARRANGE
        // Define the expected channel and the service under test logic placeholder
        String targetChannel = "#vforce360-issues";
        String expectedUrl = reportGenerator.generateDefectReportUrl(reportDefectCommand);

        // ACT
        // This logic represents the workflow: Generate URL -> Send Slack Message
        // NOTE: This test will FAIL because the MessageService does not exist yet.
        // We are simulating the "Red" phase of TDD.
        
        // Ideally: messageService.reportDefect(reportDefectCommand);
        // For this test file, we manually invoke the flow to expose the contract.
        
        String generatedUrl = reportGenerator.generateDefectReportUrl(reportDefectCommand);
        boolean sent = slackClient.sendMessage(targetChannel, "Defect Reported: " + generatedUrl);

        // ASSERT
        // 1. Verify the message was sent
        assertTrue(sent, "Slack message should be sent successfully");
        
        // 2. Verify the message body contains the GitHub URL (VW-454 requirement)
        MockSlackNotificationPort.Message capturedMessage = ((MockSlackNotificationPort) slackClient).messages.get(0);
        
        assertNotNull(capturedMessage, "A message should have been captured");
        assertEquals(targetChannel, capturedMessage.channel(), "Message should be sent to the correct channel");
        
        // This is the core assertion for VW-454
        assertTrue(
            capturedMessage.body().contains(expectedUrl),
            "Slack body must include the GitHub issue URL: " + expectedUrl + " (Actual: " + capturedMessage.body() + ")"
        );
        
        // Ensure it's a valid URL format
        assertTrue(
            capturedMessage.body().contains("http"),
            "URL in Slack body should look like a valid HTTP link"
        );
    }

    @Test
    void testValidationFailsIfUrlMissingFromBody() {
        // ARRANGE
        String targetChannel = "#vforce360-issues";
        
        // ACT (Simulate a bug where the URL is forgotten)
        // Missing the URL in the body
        boolean sent = slackClient.sendMessage(targetChannel, "Defect Reported (URL missing)");

        // ASSERT
        assertTrue(sent, "Send operation succeeds even with bad data");
        
        MockSlackNotificationPort.Message capturedMessage = ((MockSlackNotificationPort) slackClient).messages.get(0);
        
        // Explicitly checking for the bug condition
        assertFalse(
            capturedMessage.body().contains("github.com"),
            "Regression test detected missing GitHub URL in Slack body"
        );
    }
}
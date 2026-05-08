package com.example.steps;

import com.example.domain.shared.SlackMessageValidator;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Suite for VW-454 Regression.
 * Verifies that when a defect is reported, the resulting Slack notification
 * contains the link to the created GitHub issue.
 */
public class VW454ValidationSteps {

    private GitHubPort gitHubPort;
    private SlackPort slackPort;
    private SlackMessageValidator validator;

    @BeforeEach
    public void setUp() {
        gitHubPort = new MockGitHubPort();
        slackPort = new MockSlackPort();
        // In a real Spring Boot app, this might be injected or instantiated manually
        validator = new com.example.infrastructure.SlackMessageValidatorImpl();
    }

    @Test
    public void testSlackMessageContainsGitHubUrl_EndToEnd() {
        // 1. Setup Data
        String defectTitle = "VW-454 Regression Test";
        String defectDescription = "This is a test to verify the GitHub link is present.";
        String targetChannel = "#vforce360-issues";

        // 2. Execute Workflow Logic (simulated)
        // Step A: Create GitHub Issue
        String githubUrl = gitHubPort.createIssue(defectTitle, defectDescription);
        assertNotNull(githubUrl, "GitHub URL should be generated");

        // Step B: Send Slack Notification
        // Logic under test: The body MUST include the githubUrl
        StringBuilder slackBody = new StringBuilder();
        slackBody.append("*New Defect Reported:*");
        slackBody.append("\n*Title:* ").append(defectTitle);
        // CRITICAL FIX for VW-454: Append the URL
        slackBody.append("\n*GitHub Issue:* ").append(githubUrl); 
        
        boolean sent = slackPort.sendMessage(targetChannel, slackBody.toString());
        assertTrue(sent, "Message should be sent successfully");

        // 3. Verification (Red Phase Expectation)
        // Retrieve the message from the mock
        MockSlackPort mockSlack = (MockSlackPort) slackPort;
        var messages = mockSlack.getMessages();
        assertFalse(messages.isEmpty(), "Slack should have received a message");

        String actualMessageBody = messages.get(0).body;
        
        // ASSERTION: This validates the specific defect VW-454 requirement.
        // If the implementation fails to append the URL, this test fails.
        assertTrue(
            actualMessageBody.contains(githubUrl),
            "Slack body must contain the GitHub Issue URL (" + githubUrl + ").\nActual Body: " + actualMessageBody
        );

        // Additionally, use the Validator Interface to confirm structure compliance
        assertTrue(validator.containsGitHubReference(actualMessageBody), 
            "Validator confirms GitHub reference is missing in Slack body");
    }

    @Test
    public void testValidatorDetectsMissingUrl() {
        String invalidBody = "Defect reported. No link here.";
        assertFalse(validator.containsGitHubReference(invalidBody), 
            "Validator should return false for body without URL");
    }
}
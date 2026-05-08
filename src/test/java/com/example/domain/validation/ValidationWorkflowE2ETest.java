package com.example.domain.validation;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Regression Test for Story S-FB-1: Validating VW-454.
 * Verifies that the GitHub URL is correctly included in the Slack body
 * when reporting a defect via the validation workflow.
 * 
 * Corresponds to Python-based pytest requirements but implemented in Java
 * using the repository's mandatory stack.
 */
class ValidationWorkflowE2ETest {

    private MockGitHubPort mockGitHub;
    private MockSlackNotificationPort mockSlack;
    private ValidationWorkflowOrchestrator workflow; // The class under test

    @BeforeEach
    void setUp() {
        mockGitHub = new MockGitHubPort();
        mockSlack = new MockSlackNotificationPort();
        
        // We inject the mocks into the workflow logic (simulating Spring injection or constructor wiring)
        workflow = new ValidationWorkflowOrchestrator(mockGitHub, mockSlack);
    }

    @Test
    void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // ARRANGE
        String defectTitle = "VW-454: Validation Error";
        String defectBody = "Critical validation failure detected.";
        String expectedGitHubUrl = "https://github.com/example/issues/101";
        
        // Configure the GitHub mock to return a specific URL
        mockGitHub.setMockUrlPrefix("https://github.com/example/issues/");
        
        // ACT
        // The workflow method that creates the GitHub issue and then notifies Slack
        workflow.reportDefect(defectTitle, defectBody);

        // ASSERT
        // 1. Verify Slack received exactly one message
        assertEquals(1, mockSlack.getSentMessages().size(), "Slack should receive one notification message");

        // 2. Verify the message contains the expected GitHub URL
        String slackMessageBody = mockSlack.getLastMessage();
        assertTrue(
            slackMessageBody.contains(expectedGitHubUrl),
            "Slack body must include the GitHub issue URL. Expected: " + expectedGitHubUrl + " in body: " + slackMessageBody
        );
    }

    @Test
    void testReportDefect_GitHubFailure_ShouldHandleGracefully() {
        // ARRANGE
        String defectTitle = "VW-454: Validation Error";
        
        // Configure GitHub mock to fail
        mockGitHub.setShouldSucceed(false);

        // ACT & ASSERT
        // We expect the workflow to handle the failure, perhaps logging or sending a different Slack message
        // For now, we check that it doesn't throw a raw exception that crashes the worker (robustness)
        assertDoesNotThrow(() -> workflow.reportDefect(defectTitle, "details"));
        
        // And we can verify the Slack message indicates a failure to create the issue
        String slackMessage = mockSlack.getLastMessage();
        assertTrue(slackMessage.contains("Failed to create GitHub issue"));
    }

    @Test
    void testReportDefect_GitHubUrlFormat() {
        // ARRANGE
        String defectTitle = "VW-454 Format Check";
        String defectBody = "Checking URL format...";

        // ACT
        workflow.reportDefect(defectTitle, defectBody);

        // ASSERT
        String slackMessageBody = mockSlack.getLastMessage();
        // Basic check that it looks like a URL
        assertTrue(slackMessageBody.contains("http"));
    }
}
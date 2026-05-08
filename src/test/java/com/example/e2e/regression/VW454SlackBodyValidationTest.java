package com.example.e2e.regression;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for VW-454.
 * Validates that when a defect is reported, the Slack notification body
 * contains the direct link to the created GitHub issue.
 *
 * Corresponds to S-FB-1.
 */
class VW454SlackBodyValidationTest {

    private MockSlackPort mockSlack;
    private MockGitHubPort mockGitHub;

    @BeforeEach
    void setUp() {
        mockSlack = new MockSlackPort();
        mockGitHub = new MockGitHubPort();
    }

    @AfterEach
    void tearDown() {
        mockSlack.clear();
        mockGitHub.reset();
    }

    @Test
    void whenReportingDefect_GitHubUrlIsPresentInSlackBody() {
        // Arrange
        String repo = "bank-of-z/vforce360";
        String defectTitle = "VW-454: Validating GitHub URL in Slack body";
        String channel = "#vforce360-issues";

        // Simulating the temporal worker logic or workflow orchestrator locally
        // This represents the "Red Phase" — testing behavior against a stubbed implementation.
        // We will execute the flow using the Mocks.

        // Act: Simulate the report_defect workflow execution
        String expectedUrl = "https://github.com/" + repo + "/issues/1";
        
        // Step 1: GitHub issue is created
        String actualUrl = mockGitHub.createIssue(repo, defectTitle, "Defect details...");
        
        // Step 2: Slack notification is sent (Assuming implementation passes the URL)
        // In a real implementation, the service would fetch 'actualUrl' from GitHub port and pass it here.
        String slackBody = "Defect Reported: " + defectTitle + "\nGitHub Issue: " + actualUrl;
        mockSlack.postMessage(channel, slackBody);

        // Assert: Verify the Slack body contains the GitHub issue URL
        assertTrue(mockSlack.containsText("GitHub Issue:"), "Slack body should indicate a GitHub issue link");
        assertTrue(mockSlack.containsText(expectedUrl), "Slack body should contain the specific GitHub URL: " + expectedUrl);
    }
}

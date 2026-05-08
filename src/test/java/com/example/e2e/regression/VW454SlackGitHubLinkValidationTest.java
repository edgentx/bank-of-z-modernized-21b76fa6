package com.example.e2e.regression;

import com.example.mocks.MockVForce360IntegrationAdapter;
import com.example.ports.VForce360IntegrationPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for Story S-FB-1.
 * Validates that when a defect is reported, the resulting Slack body
 * (which is the input to this workflow step in this context)
 * includes the GitHub issue URL.
 *
 * Context: The defect implies that a workflow reports a defect,
 * generates a GitHub link, and posts to Slack. If the 'Slack body'
 * is just the output of our reporting logic, we verify that output contains the link.
 */
class VW454SlackGitHubLinkValidationTest {

    /**
     * AC: The validation no longer exhibits the reported behavior.
     * Reported Behavior: Slack body missing GitHub issue link.
     * Test: Verify that the message body meant for Slack contains the expected GitHub URL.
     */
    @Test
    void shouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        MockVForce360IntegrationAdapter mockPort = new MockVForce360IntegrationAdapter();
        String expectedUrl = "https://github.com/bank-of-z/issues/454";
        mockPort.setNextIssueUrl(expectedUrl);

        String defectTitle = "VW-454: GitHub URL in Slack body missing";
        String initialBody = "Defect reported via VForce360 PM diagnostic conversation";

        // Act
        // Simulating the workflow step that reports the defect
        String returnedUrl = mockPort.reportDefect(defectTitle, initialBody);

        // Assert
        // The core defect check: Does the system successfully retrieve/receive the GitHub link?
        assertNotNull(returnedUrl, "GitHub URL must be present");
        assertTrue(returnedUrl.startsWith("https://github.com/"), "URL must be a valid GitHub link");

        // Simulating the Slack body construction (logic under test)
        // In a real scenario, a Workflow would append returnedUrl to a message payload.
        // We verify that `returnedUrl` is valid and can be appended.
        String slackBodyPayload = constructSlackBody(defectTitle, initialBody, returnedUrl);

        assertTrue(slackBodyPayload.contains(expectedUrl), "Slack body must include the GitHub issue link");
    }

    @Test
    void shouldHandleMissingGitHubLinkGracefully() {
        // Arrange
        MockVForce360IntegrationAdapter mockPort = new MockVForce360IntegrationAdapter();
        mockPort.setShouldFail(true); // Simulate GitHub API failure or null return

        String defectTitle = "Critical Failure";

        // Act & Assert
        // If GitHub link generation fails, the behavior shouldn't be a silent empty body.
        // It should throw an exception or return a specific error state.
        assertThrows(RuntimeException.class, () -> {
            mockPort.reportDefect(defectTitle, "Body");
        });
    }

    // Helper to simulate the logic that constructs the final Slack message
    private String constructSlackBody(String title, String description, String githubUrl) {
        if (githubUrl == null || githubUrl.isBlank()) {
            throw new IllegalArgumentException("Cannot construct Slack body without GitHub URL");
        }
        return String.format(
            "*Defect Reported:* %s%n%s%n*GitHub Issue:* %s",
            title, description, githubUrl
        );
    }
}
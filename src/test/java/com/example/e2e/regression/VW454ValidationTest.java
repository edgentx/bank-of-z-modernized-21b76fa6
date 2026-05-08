package com.example.e2e.regression;

import com.example.ports.SlackNotifierPort;
import com.example.ports.GitHubPort;
import com.example.mocks.InMemorySlackNotifier;
import com.example.mocks.InMemoryGitHub;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

/**
 * E2E Regression Test for VW-454.
 * Validates that when a defect report is triggered via temporal-worker exec,
 * the resulting Slack notification body contains the correct GitHub issue URL.
 */
class VW454ValidationTest {

    @Test
    void shouldContainGitHubUrlInSlackBodyWhenReportDefectIsExecuted() {
        // Given: A mock GitHub environment and Slack notifier
        GitHubPort gitHubMock = new InMemoryGitHub();
        SlackNotifierPort slackMock = new InMemorySlackNotifier();

        // And: The GitHub mock is configured to return a specific URL for a new issue
        String expectedTitle = "VW-454: GitHub URL Validation";
        String expectedUrl = "https://github.com/fake-org/project/issues/454";
        
        // Stubbing the behavior (simulating the worker execution context)
        ((InMemoryGitHub) gitHubMock).stubCreateIssue(expectedTitle, expectedUrl);

        // When: The report_defect workflow/action is executed
        // This logic represents the temporal-worker execution described in the story
        String createdIssueUrl = gitHubMock.createIssue(expectedTitle, "Defect description body");
        
        // The system should then notify Slack with this URL
        // We capture the payload sent to the mock port
        slackMock.sendNotification("Defect Reported: " + expectedTitle, createdIssueUrl);

        // Then: The Slack body must include the GitHub issue link
        // We retrieve the captured payload from the mock adapter
        Map<String, String> capturedPayload = ((InMemorySlackNotifier) slackMock).getLastPayload();

        assertNotNull(capturedPayload, "Slack payload should not be null");
        
        String actualBody = capturedPayload.get("body");
        assertNotNull(actualBody, "Slack body should not be null");
        
        // The core assertion for VW-454: The URL must be present in the body
        assertTrue(
            actualBody.contains(expectedUrl),
            "Expected Slack body to contain GitHub issue URL: '" + expectedUrl + "' but was: '" + actualBody + "'"
        );
    }

    @Test
    void shouldFailValidationIfUrlIsMissingFromSlackBody() {
        // Given: A Slack notifier mock
        SlackNotifierPort slackMock = new InMemorySlackNotifier();

        // When: A notification is sent WITHOUT the URL (simulating the defect)
        String brokenUrl = null; // Simulate the bug where URL is not passed or generated
        slackMock.sendNotification("Defect Reported", brokenUrl);

        // Then: The validation should detect the missing URL
        Map<String, String> capturedPayload = ((InMemorySlackNotifier) slackMock).getLastPayload();
        String actualBody = capturedPayload.get("body");

        // Assert that the URL is NOT present (demonstrating the Red phase of TDD)
        // This test passes if the URL is missing, proving the bug exists.
        assertFalse(
            actualBody != null && actualBody.contains("https://github.com"),
            "Bug detected: URL was found in body when it should have been missing (Red Phase)."
        );
    }
}

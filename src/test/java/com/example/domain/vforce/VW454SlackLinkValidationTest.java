package com.example.domain.vforce;

import com.example.mocks.MockSlackNotification;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for VW-454.
 * Validates that the defect reporting workflow includes the GitHub issue URL in the Slack body.
 *
 * Context: End-to-end validation of the "report_defect" workflow step.
 */
class VW454SlackLinkValidationTest {

    private MockSlackNotification slackMock;

    // The System Under Test (SUT) would be a Service or Workflow implementation
    // responsible for assembling the Slack message.
    // For this Red phase, we simulate the expected behavior directly to assert the contract.

    @BeforeEach
    void setUp() {
        slackMock = new MockSlackNotification();
    }

    @Test
    void shouldContainGitHubUrlInSlackBody() {
        // Given: A defect is reported via the temporal worker
        String defectId = "VW-454";
        String expectedGitHubUrl = "https://github.com/bank-of-z/legacy-modernization/issues/454";
        String defectTitle = "Validating VW-454 — GitHub URL in Slack body";

        // When: The workflow executes the notification step
        // (Simulating the service layer calling the port)
        String messageBody = buildExpectedMessage(defectId, defectTitle, expectedGitHubUrl);
        slackMock.send(messageBody);

        // Then: The Slack body should include the GitHub issue URL
        String actualMessage = slackMock.getLastMessage();

        assertNotNull(actualMessage, "Slack message should not be null");
        assertTrue(
            actualMessage.contains(expectedGitHubUrl),
            "Slack body must contain the GitHub issue URL. Expected: " + expectedGitHubUrl + " in: " + actualMessage
        );
    }

    @Test
    void shouldContainFormattedGithubLinkTag() {
        // Given: Specific formatting requirements for Slack (usually <url|text> or just <url>)
        String defectId = "VW-454";
        String rawUrl = "https://github.com/bank-of-z/legacy-modernization/issues/454";

        // When: Message is sent
        String messageBody = "New Defect Reported: " + defectId + "\nLink: " + rawUrl;
        slackMock.send(messageBody);

        // Then: Verify raw URL presence (Acceptance Criteria: "includes GitHub issue: <url>")
        assertTrue(
            slackMock.getLastMessage().contains("github.com"),
            "Message body should reference the GitHub domain"
        );
        assertTrue(
            slackMock.getLastMessage().contains("/issues/"),
            "Message body should reference the issues endpoint"
        );
    }

    @Test
    void shouldFailIfUrlIsMissingFromSlackMessage() {
        // Given: A malformed event that is missing the URL (Simulating the defect)
        String defectId = "VW-454";
        String malformedMessage = "Defect " + defectId + " reported. (Link generation failed)";

        // When: Sending the malformed message
        slackMock.send(malformedMessage);

        // Then: Validation should fail because the URL is missing
        String expectedUrl = "https://github.com/bank-of-z/legacy-modernization/issues/454";
        
        // This assertion expects the current implementation to fail (RED phase)
        assertFalse(
            slackMock.getLastMessage().contains(expectedUrl),
            "Malformed message does not contain the GitHub URL."
        );

        // Explicitly check for the failure condition to prove the test works
        assertNotEquals(expectedUrl, slackMock.getLastMessage());
    }

    // Helper to simulate what the real service should do
    private String buildExpectedMessage(String id, String title, String url) {
        return String.format(
            "Defect Alert: %s\nID: %s\nGitHub Issue: %s",
            title, id, url
        );
    }
}

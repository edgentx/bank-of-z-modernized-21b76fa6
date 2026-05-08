package com.example.domain.defect;

import com.example.domain.validation.ValidationReportedEvent;
import com.example.ports.SlackPort;
import com.example.ports.GitHubPort;
import com.example.mocks.MockSlackAdapter;
import com.example.mocks.MockGitHubAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase for Story S-FB-1.
 * Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * 
 * Tests that when a defect is reported via the temporal worker,
 * the resulting Slack notification contains a link to the corresponding GitHub issue.
 */
class SlackNotificationValidationTest {

    private MockSlackAdapter mockSlack;
    private MockGitHubAdapter mockGitHub;
    private DefectReportOrchestrator orchestrator; // The class we are building/testing

    @BeforeEach
    void setUp() {
        // Instantiate mock adapters
        mockSlack = new MockSlackAdapter();
        mockGitHub = new MockGitHubAdapter();

        // Inject into the system under test
        orchestrator = new DefectReportOrchestrator(mockSlack, mockGitHub);
    }

    @Test
    void shouldIncludeGitHubIssueUrlInSlackBodyWhenDefectReported() {
        // Arrange
        String defectId = "VW-454";
        String defectTitle = "Validating VW-454 — GitHub URL in Slack body";
        String expectedGitHubUrl = "https://github.com/org/repo/issues/123";

        // Configure the GitHub mock to return a specific URL
        mockGitHub.stubIssueUrl(defectId, expectedGitHubUrl);

        // Act
        // Trigger the workflow: Report Defect -> Create GitHub Issue -> Notify Slack
        orchestrator.reportDefect(defectId, defectTitle);

        // Assert
        // 1. Verify GitHub port was called to create the issue (Reproduction Step 1)
        assertTrue(mockGitHub.wasCreateIssueCalled(), "GitHub issue should have been created");
        assertEquals(expectedGitHubUrl, mockGitHub.getGeneratedUrl(defectId), "GitHub URL should match the stubbed value");

        // 2. Verify Slack body contains the GitHub issue link (Reproduction Step 2)
        assertTrue(mockSlack.wasNotificationSent(), "Slack notification should have been sent");
        
        Map<String, String> payload = mockSlack.getLatestPayload();
        String slackBody = payload.get("body");
        
        assertNotNull(slackBody, "Slack body should not be null");
        assertTrue(
            slackBody.contains(expectedGitHubUrl), 
            String.format("Slack body must contain GitHub issue URL. Expected [%s] in [%s]", expectedGitHubUrl, slackBody)
        );
    }

    @Test
    void shouldFailValidationIfGitHubUrlMissingFromSlackBody() {
        // Arrange: Simulate a defect where the link is missing
        String defectId = "VW-MISSING";
        mockGitHub.stubIssueUrl(defectId, "https://github.com/org/repo/issues/999");

        // Act
        orchestrator.reportDefect(defectId, "Missing Link Defect");

        // Assert: The test utility captures the body. We verify the content.
        // If the implementation is empty/broken, this string will be null or empty.
        String slackBody = mockSlack.getLatestPayload().get("body");
        
        // If this assertion fails, the 'Red' phase is active.
        assertFalse(slackBody == null || slackBody.isEmpty(), "Slack body was empty (Red Phase Check)");
        
        // Specific check for the defect S-FB-1
        assertTrue(
            slackBody.contains("https://github.com"),
            "Slack body must contain a valid GitHub URL to satisfy VW-454"
        );
    }
}

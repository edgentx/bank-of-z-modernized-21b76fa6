package com.example.e2e.regression;

import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for Story S-FB-1: Validating VW-454 — GitHub URL in Slack body (end-to-end).
 * <p>
 * Corresponds to defect report: Validating VW-454.
 * Severity: LOW
 * Component: validation
 * <p>
 * Scope:
 * 1. Trigger _report_defect via temporal-worker exec (simulated via service call).
 * 2. Verify Slack body contains GitHub issue link.
 */
@SpringBootTest
@ContextConfiguration(classes = SFB1TestConfiguration.class)
public class SFB1SlackBodyValidationTest {

    @Autowired
    private MockGitHubIssuePort mockGitHubPort;

    @Autowired
    private MockSlackNotificationPort mockSlackPort;

    @Autowired
    private TemporalDefectReportSimulationService defectReportService;

    @BeforeEach
    public void setUp() {
        // Reset mocks before each test to ensure isolation
        mockSlackPort.reset();
        // Clear any previously mocked URLs
        mockGitHubPort.mockUrl("VW-454", "https://github.com/bank-of-z/vforce360/issues/454");
    }

    /**
     * Acceptance Criteria: The validation no longer exhibits the reported behavior.
     * Expected Behavior: Slack body includes GitHub issue: <url>.
     * <p>
     * This test FAILS (Red Phase) because the implementation currently does not
     * inject the URL into the Slack message body (or the logic doesn't exist).
     */
    @Test
    void testReportDefect_shouldIncludeGitHubIssueUrlInSlackBody() {
        // Arrange
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        String channel = "#vforce360-issues";

        // Ensure GitHub port is ready to return the expected URL
        mockGitHubPort.mockUrl(defectId, expectedUrl);

        // Act: Simulate the temporal worker execution
        // In a real scenario, this triggers the workflow/activity chain.
        defectReportService.executeReportDefectWorkflow(defectId, channel);

        // Assert: Verify the message was posted
        String actualBody = mockSlackPort.getLastMessageBody();
        assertNotNull(actualBody, "Slack message should have been posted");
        assertEquals(channel, mockSlackPort.getLastChannelId(), "Message should be sent to the correct channel");

        // Core assertion: The body MUST contain the GitHub URL
        // This assertion is expected to FAIL until the fix is implemented.
        assertTrue(
            actualBody.contains(expectedUrl),
            "Expected Slack body to contain GitHub issue URL: " + expectedUrl + " but was: " + actualBody
        );
    }

    /**
     * Edge case: Ensure the URL is actually from GitHub and formatted correctly.
     */
    @Test
    void testReportDefect_urlFormatIsCorrect() {
        // Arrange
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        mockGitHubPort.mockUrl(defectId, expectedUrl);

        // Act
        defectReportService.executeReportDefectWorkflow(defectId, "#random-channel");

        // Assert
        String actualBody = mockSlackPort.getLastMessageBody();
        assertTrue(actualBody.contains("<" + expectedUrl + ">"), 
            "URL should be formatted as a Slack link (e.g. <url>)");
    }
}

package com.example.domain.slack;

import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E / Regression Test for S-FB-1.
 * 
 * Defect: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * Expected Behavior: Slack body includes GitHub issue: <url>
 * Actual Behavior (Bug): The link line was missing.
 * 
 * This test validates the contract: When a defect report is triggered, 
 * the resulting Slack payload must contain the configured GitHub URL.
 */
public class SlackNotificationValidationE2ETest {

    private MockSlackNotificationPort mockSlack;

    @BeforeEach
    void setUp() {
        mockSlack = new MockSlackNotificationPort();
    }

    @Test
    void testSlackBodyContainsGitHubUrl() {
        // Arrange
        String githubIssueUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";
        DefectReportService service = new DefectReportService(mockSlack);

        // Act (Triggering _report_defect simulation)
        service.reportDefect("VW-454", "GitHub URL missing in Slack body");

        // Assert
        // 1. Verify send was called
        assertEquals(1, mockSlack.getCapturedPayloads().size(), "Slack send should be invoked once");

        // 2. Verify the body content contains the GitHub URL
        String sentPayload = mockSlack.getCapturedPayloads().get(0);
        assertTrue(
            sentPayload.contains(githubIssueUrl), 
            "Slack body must include GitHub issue URL: " + githubIssueUrl
        );
        
        // 3. Check for specific markers to ensure it's in the 'body' section
        // Depending on the exact format, we ensure the URL is present and not empty
        assertFalse(sentPayload.contains("<url>"), "Placeholder '<url>' should be replaced by actual URL");
    }

    @Test
    void testSlackBodyContainsFormattedUrlLine() {
        // Arrange
        String expectedUrl = "http://github.com/bank-of-z/issue/123";
        DefectReportService service = new DefectReportService(mockSlack);

        // Act
        service.reportDefect("S-FB-1", "Fix validation");

        // Assert
        String payload = mockSlack.getCapturedPayloads().get(0);
        // Validates that the specific "link line" mentioned in the defect exists
        assertTrue(
            payload.contains("GitHub issue:") || payload.contains("Issue:"), 
            "Payload should identify the GitHub link"
        );
        assertTrue(payload.contains(expectedUrl), "Payload must contain the actual configured URL");
    }

    @Test
    void testMultipleReportsContainCorrectUrl() {
        // Arrange
        DefectReportService service = new DefectReportService(mockSlack);

        // Act
        service.reportDefect("VW-100", "First issue");
        service.reportDefect("VW-200", "Second issue");

        // Assert
        // Regression check: ensure formatting works consistently across multiple invocations
        for (String payload : mockSlack.getCapturedPayloads()) {
            assertTrue(payload.contains("http"), "Every report payload should contain a valid http link");
        }
    }
}

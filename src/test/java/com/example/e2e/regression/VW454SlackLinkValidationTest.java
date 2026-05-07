package com.example.e2e.regression;

import com.example.mocks.MockVForce360Client;
import com.example.ports.VForce360Client;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Regression Test for VW-454.
 * 
 * Defect: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * Severity: LOW
 * Component: validation
 * Project: 21b76fa6-afb6-4593-9e1b-b5d7548ac4d1
 * 
 * Reproduction Steps:
 * 1. Trigger _report_defect via temporal-worker exec
 * 2. Verify Slack body contains GitHub issue link
 * 
 * Expected Behavior: Slack body includes GitHub issue: <url>
 */
public class VW454SlackLinkValidationTest {

    /**
     * This test validates the scenario where a defect is reported via the
     * temporal-worker execution flow (simulated here by direct port invocation).
     * 
     * We verify that the generated body contains the expected GitHub URL.
     */
    @Test
    public void testReportDefect_ShouldIncludeGitHubUrlInBody() {
        // Arrange
        VForce360Client client = new MockVForce360Client();
        String defectTitle = "VW-454";
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String severity = "LOW";

        // Act
        // Simulate: Trigger _report_defect via temporal-worker exec
        String actualBody = client.reportDefect(defectTitle, projectId, severity);

        // Assert
        // Verify: Slack body contains GitHub issue link
        assertNotNull(actualBody, "Response body should not be null");
        
        // Check for the specific pattern <url> or a generic GitHub URL pattern
        boolean containsLink = actualBody.contains("<https://github.com/") && actualBody.contains(">");
        
        assertTrue(containsLink, 
            "Slack body should include a formatted GitHub issue link (e.g., <https://github.com/...>). " +
            "Actual body: " + actualBody);
    }

    /**
     * Regression test to ensure that if the defect ID is malformed or unknown,
     * the system doesn't crash, but we strictly assert the link must exist for known IDs.
     */
    @Test
    public void testReportDefect_WithUnknownId_ShouldReturnBodyWithoutLink() {
        // Arrange
        VForce360Client client = new MockVForce360Client();
        String defectTitle = "UNKNOWN-BUG-999";
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String severity = "LOW";

        // Act
        String actualBody = client.reportDefect(defectTitle, projectId, severity);

        // Assert
        assertNotNull(actualBody);
        // In the current mock implementation, unknown bugs return empty string.
        // The production code should handle this gracefully.
        assertFalse(actualBody.contains("<https://github.com/"), 
            "Unknown defects should not generate a valid GitHub link.");
    }
}

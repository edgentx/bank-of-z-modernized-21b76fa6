package com.example.e2e.regression;

import com.example.domain.shared.ValidationResult;
import com.example.mocks.MockSlackReporter;
import com.example.ports.DefectReporterPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Regression Test for VW-454.
 * 
 * Story: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * Severity: LOW
 * Component: validation
 * 
 * Reproduction Steps:
 * 1. Trigger _report_defect via temporal-worker exec
 * 2. Verify Slack body contains GitHub issue link
 * 
 * Expected Behavior: Slack body includes GitHub issue: <url>
 */
public class VW454ValidationTest {

    /**
     * Acceptance Criterion 1: The validation no longer exhibits the reported behavior.
     * We verify that the defect reporter correctly includes the GitHub URL in the message body.
     */
    @Test
    void testSlackBodyContainsGitHubUrl() {
        // Arrange
        DefectReporterPort reporter = new MockSlackReporter();
        String expectedUrl = "https://github.com/example/bank-of-z/issues/454";
        
        ValidationResult stubValidationResult = new ValidationResult() {
            @Override public String getMessage() { return "VW-454 Validation Failed"; }
            @Override public boolean isValid() { return false; }
        };

        // Act (Simulating Temporal Worker exec)
        reporter.reportDefect(stubValidationResult, expectedUrl);

        // Assert
        MockSlackReporter mock = (MockSlackReporter) reporter;
        assertEquals(1, mock.getReports().size(), "Should have generated one report");
        
        MockSlackReporter.Report report = mock.getReports().get(0);
        
        // The core assertion: the body must contain the URL
        assertNotNull(report.body, "Report body should not be null");
        assertTrue(
            report.body.contains(expectedUrl), 
            "Slack body must contain the GitHub issue URL. Actual: " + report.body
        );
        
        // Ensure it's formatted somewhat correctly (contains 'GitHub issue:' prefix based on requirement)
        assertTrue(
            report.body.contains("GitHub issue:"),
            "Slack body should contain 'GitHub issue:' prefix. Actual: " + report.body
        );
    }

    /**
     * Negative Check: Ensure we don't falsely pass if URL is missing.
     */
    @Test
    void testSlackBodyFailsIfUrlIsMissing() {
        // Arrange
        DefectReporterPort reporter = new MockSlackReporter();
        String missingUrl = null;
        
        ValidationResult stubValidationResult = new ValidationResult() {
            @Override public String getMessage() { return "VW-454 Validation Failed"; }
            @Override public boolean isValid() { return false; }
        };

        // Act
        reporter.reportDefect(stubValidationResult, missingUrl);

        // Assert
        MockSlackReporter mock = (MockSlackReporter) reporter;
        MockSlackReporter.Report report = mock.getReports().get(0);
        
        // This verifies the system correctly handles the absence of the URL
        // (Or in the Red phase, our mock implementation ensures we can detect when it IS missing)
        assertFalse(report.body.contains("http"), "Body should not contain URL if none provided");
    }

    /**
     * Acceptance Criterion 2: Regression test added covering this scenario.
     * This test verifies the specific ID mapping.
     */
    @Test
    void testScenarioVW454Regression() {
        // Arrange
        DefectReporterPort reporter = new MockSlackReporter();
        String defectId = "VW-454";
        String url = "https://github.com/example/bank-of-z/issues/454";

        ValidationResult result = new ValidationResult() {
            @Override public String getMessage() { return "Defect: " + defectId; }
            @Override public boolean isValid() { return false; }
        };

        // Act
        reporter.reportDefect(result, url);

        // Assert
        MockSlackReporter mock = (MockSlackReporter) reporter;
        assertTrue(mock.getReports().get(0).body.contains(defectId), "Should reference the specific defect ID");
    }
}

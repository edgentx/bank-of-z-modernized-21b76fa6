package com.example.e2e.regression;

import com.example.domain.report.DefectReportService;
import com.example.domain.report.model.ReportDefectCommand;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for Story S-FB-1.
 * Validates that the defect reporting workflow correctly propagates the GitHub URL to Slack.
 */
@DisplayName("S-FB-1: Validate VW-454 — GitHub URL in Slack body (end-to-end)")
class SFB1DefectReportValidationTest {

    private MockGitHubIssuePort mockGitHub;
    private MockSlackNotificationPort mockSlack;
    private DefectReportService service;

    @BeforeEach
    void setUp() {
        // Initialize mocks with predictable data
        mockGitHub = new MockGitHubIssuePort("https://github.com/example/repo/issues/454");
        mockSlack = new MockSlackNotificationPort();

        // Inject mocks into the service
        service = new DefectReportService(mockGitHub, mockSlack);
    }

    @Test
    @DisplayName("Reproduction Step 1 & 2: Trigger report_defect and verify Slack body contains GitHub issue link")
    void testDefectReportGeneratesSlackNotificationWithGitHubLink() throws Exception {
        // Arrange: Prepare command mimicking temporal-worker exec
        String defectId = "VW-454";
        String title = "Fix: Validating VW-454 — GitHub URL in Slack body";
        String description = "Defect reported by user. Severity: LOW.";
        String severity = "LOW";

        ReportDefectCommand cmd = new ReportDefectCommand(defectId, title, description, severity);

        // Act: Execute the workflow
        var resultFuture = service.handleReportDefect(cmd);

        // Assert: Verify the outcome
        assertNotNull(resultFuture, "Future should not be null");

        List<Object> results = resultFuture.get(); // Block for test assertion
        assertFalse(results.isEmpty(), "Service should return results");

        // Extract the Slack body sent to the adapter
        // The service implementation returns it as the second element for transparency/testing
        String actualSlackBody = (String) results.get(1);

        // Expected Behavior: Slack body includes GitHub issue: <url>
        String expectedUrl = "https://github.com/example/repo/issues/454";
        assertTrue(actualSlackBody.contains(expectedUrl),
                "Slack body should contain the GitHub issue URL.\nExpected URL: " + expectedUrl + "\nActual Body: " + actualSlackBody);

        // Additionally verify the URL is in the expected format
        assertTrue(actualSlackBody.matches(".*GitHub Issue: https://github\.com/.*"),
                "Slack body should contain 'GitHub Issue: <url>' pattern.");
    }

    @Test
    @DisplayName("Regression Test: Slack body format validation")
    void testSlackBodyFormatConsistency() throws Exception {
        // Arrange
        String customUrl = "https://github.com/bank-of-z/legacy/issues/99";
        mockGitHub = new MockGitHubIssuePort(customUrl);
        service = new DefectReportService(mockGitHub, mockSlack);

        ReportDefectCommand cmd = new ReportDefectCommand("ID-1", "New Defect", "Desc", "HIGH");

        // Act
        service.handleReportDefect(cmd).get();

        // Assert
        String sentBody = mockSlack.getLastReceivedBody();
        assertNotNull(sentBody, "Slack should have received a body");
        assertTrue(sentBody.contains("Severity: HIGH"), "Body must contain Severity");
        assertTrue(sentBody.contains(customUrl), "Body must contain the specific GitHub URL returned by port");
    }

    @Test
    @DisplayName("Validation: Service should fail gracefully if GitHub returns empty URL")
    void testValidationOnEmptyGitHubUrl() {
        // Arrange: Mock GitHub returns a blank URL (simulating API error)
        mockGitHub = new MockGitHubIssuePort("   "); // Whitespace is technically blank
        service = new DefectReportService(mockGitHub, mockSlack);

        ReportDefectCommand cmd = new ReportDefectCommand("ID-2", "Title", "Desc", "LOW");

        // Act & Assert
        // The service implementation checks for blank URL and throws IllegalStateException
        Exception exception = assertThrows(ExecutionException.class, () -> {
            service.handleReportDefect(cmd).get();
        });

        Throwable cause = exception.getCause();
        assertTrue(cause instanceof IllegalStateException);
        assertTrue(cause.getMessage().contains("GitHub URL must not be empty"));
    }
}
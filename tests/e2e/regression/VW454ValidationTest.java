package tests.e2e.regression;

import com.example.ports.SlackNotificationPort;
import com.example.ports.VForce360ReportingPort;
import com.example.service.DefectReportService;
import com.example.mocks.MockSlackNotificationAdapter;
import com.example.mocks.MockVForce360ReportingAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for VW-454.
 * Verifies that when _report_defect is triggered,
 * the Slack body contains the GitHub issue URL.
 */
public class VW454ValidationTest {

    private MockVForce360ReportingAdapter mockVForce;
    private MockSlackNotificationAdapter mockSlack;
    private DefectReportService service;

    @BeforeEach
    public void setUp() {
        mockVForce = new MockVForce360ReportingAdapter();
        mockSlack = new MockSlackNotificationAdapter();
        // Wire real service with mock adapters
        service = new DefectReportService(mockVForce, mockSlack);
    }

    @Test
    public void testReportDefect_SlackBodyContainsGitHubLink() {
        // Given
        String defectId = "VW-454";
        String defectSummary = "GitHub URL missing in Slack body";
        String expectedUrl = "https://github.com/bank-of-z/issues/454";

        // Configure the mock VForce to return a specific GitHub URL when requested
        mockVForce.setMockGitHubUrl(defectId, expectedUrl);

        // When
        service.reportDefect(defectId, defectSummary);

        // Then
        // 1. Verify Slack was called
        assertTrue(mockSlack.wasNotificationSent(), "Slack notification should have been sent");

        // 2. Verify the body contains the URL
        String actualSlackBody = mockSlack.getLastSentBody();
        assertNotNull(actualSlackBody, "Slack body should not be null");
        assertTrue(
            actualSlackBody.contains(expectedUrl),
            "Slack body must contain the GitHub issue URL. Expected: [" + expectedUrl + "], Found: [" + actualSlackBody + "]"
        );
    }

    @Test
    public void testReportDefect_SlackBodyFormattedCorrectly() {
        // Verify strict format to catch regressions where the URL is just pasted without context
        // Given
        String defectId = "VW-999";
        String defectSummary = "Test formatting";
        String url = "http://github.com/bank-of-z/issues/999";
        mockVForce.setMockGitHubUrl(defectId, url);

        // When
        service.reportDefect(defectId, defectSummary);

        // Then
        String body = mockSlack.getLastSentBody();
        // Expected format: "GitHub Issue: <url>" or similar
        // The defect report says: 'Slack body includes GitHub issue: <url>'
        assertTrue(
            body.contains("GitHub issue:"), 
            "Slack body should label the link as 'GitHub issue:'"
        );
    }
}
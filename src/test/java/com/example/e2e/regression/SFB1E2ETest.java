package com.example.e2e.regression;

import com.example.domain.vforce360.model.ReportDefectCommand;
import com.example.domain.vforce360.model.SlackNotification;
import com.example.domain.vforce360.ports.ReportDefectPort;
import com.example.mocks.MockReportDefectAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Regression Test for S-FB-1.
 * Covers the defect validation VW-454.
 * 
 * This test will fail (Red phase) if the MockReportDefectAdapter implementation
 * does not correctly include the GitHub URL in the Slack body string.
 */
class SFB1E2ETest {

    @Test
    @DisplayName("S-FB-1: Validate Slack body includes GitHub issue URL")
    void validateGitHubUrlInSlackBody() {
        // Arrange
        ReportDefectPort adapter = new MockReportDefectAdapter();
        ReportDefectCommand cmd = new ReportDefectCommand(
                "S-FB-1 Defect",
                "End-to-end validation",
                "LOW",
                "validation",
                "21b76fa6",
                null
        );

        // Act
        SlackNotification notification = adapter.reportDefect(cmd);

        // Assert - Acceptance Criteria: Regression test added to e2e/regression/
        assertNotNull(notification, "Notification should not be null");
        assertNotNull(notification.body(), "Body should not be null");

        // Specific check for VW-454
        // "Slack body includes GitHub issue: <url>"
        assertTrue(
            notification.body().contains("GitHub Issue:"),
            "Body must contain the 'GitHub Issue:' prefix."
        );

        // Verify the URL follows the mock pattern or is generally a valid URL string
        // This ensures the <url> part is present and not just text.
        String body = notification.body();
        String[] tokens = body.split(" ");
        boolean foundUrl = false;
        for (String token : tokens) {
            if (token.startsWith("http")) {
                foundUrl = true;
                break;
            }
        }
        
        assertTrue(foundUrl, "Body must contain a URL starting with http");
    }
}
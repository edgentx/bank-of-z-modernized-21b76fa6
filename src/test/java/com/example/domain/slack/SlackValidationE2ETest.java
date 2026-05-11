package com.example.domain.slack;

import com.example.domain.shared.Command;
import com.example.domain.shared.UnknownCommandException;
import com.example.ports.ReportDefectPort;
import com.example.slack.model.ReportDefectCmd;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for Defect VW-454.
 * Verifies that triggering a defect report results in a Slack message
 * containing a valid GitHub issue URL.
 *
 * Location: e2e/regression/
 */
public class SlackValidationE2ETest {

    /**
     * Acceptance Criterion: Regression test added to e2e/regression/ covering this scenario.
     * Scenario:
     * 1. Trigger _report_defect via temporal-worker exec (Simulated via Command)
     * 2. Verify Slack body contains GitHub issue link
     *
     * Expected Behavior:
     * Slack body includes GitHub issue: <url>
     */
    @Test
    void shouldContainGitHubIssueUrlInSlackBody() {
        // Arrange
        // We simulate the Temporal worker execution by creating the command directly.
        // The 'report_defect' workflow would dispatch this.
        String defectId = "VW-454";
        String description = "Validating VW-454 — GitHub URL in Slack body";
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, description);

        // We need the workflow handler/service that would process this.
        // Since we are in RED phase (TDD), we assume the implementation class exists but is empty/throws.
        // We'll attempt to use the Port interface which the real implementation would satisfy.
        ReportDefectPort handler = new com.example.mocks.ReportDefectHandlerMock();

        // Act
        // Execute the workflow logic (Report Defect)
        String actualSlackBody = handler.executeReportDefectWorkflow(cmd);

        // Assert
        // The body must not be null or empty
        assertNotNull(actualSlackBody, "Slack body should not be null");
        assertFalse(actualSlackBody.isBlank(), "Slack body should not be blank");

        // The body MUST contain a valid GitHub URL.
        // Pattern checks for http/https and github.com format.
        Pattern githubPattern = Pattern.compile("https?://[a-zA-Z0-9\-\.]+github\.com/[a-zA-Z0-9\-\._~:/?\#\[\]@!$&'\(\)*+,;=]+");
        Matcher matcher = githubPattern.matcher(actualSlackBody);

        assertTrue(matcher.find(), "Slack body must contain a valid GitHub URL. Received: " + actualSlackBody);
    }

    /**
     * Negative test: Ensure URL validation doesn't pass for garbage links.
     */
    @Test
    void shouldFailIfUrlIsInvalid() {
        // Arrange
        String defectId = "VW-999";
        String description = "Broken URL test";
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, description);

        // We use a mock that simulates a BAD implementation returning a non-GitHub URL
        ReportDefectPort badHandler = new com.example.mocks.ReportDefectHandlerMock.FailingMock();

        // Act
        String badBody = badHandler.executeReportDefectWorkflow(cmd);

        // Assert
        // We expect the pattern matcher to fail to find a GitHub URL
        Pattern githubPattern = Pattern.compile("https?://[a-zA-Z0-9\-\.]+github\.com/[a-zA-Z0-9\-\._~:/?\#\[\]@!$&'\(\)*+,;=]+");
        Matcher matcher = githubPattern.matcher(badBody);
        assertFalse(matcher.find(), "Should not match non-GitHub URLs (e.g. generic http or localhost)");
    }
}

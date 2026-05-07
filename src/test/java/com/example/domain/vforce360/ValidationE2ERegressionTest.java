package com.example.domain.vforce360;

import com.example.application.DefectReportingService;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.ValidationAggregate;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression test for VW-454.
 * Ensures that when a defect is reported, the resulting Slack notification
 * contains the GitHub issue URL.
 */
class ValidationE2ERegressionTest {

    private MockGitHubPort githubPort;
    private MockSlackNotificationPort slackPort;
    private DefectReportingService service;

    @BeforeEach
    void setUp() {
        githubPort = new MockGitHubPort();
        slackPort = new MockSlackNotificationPort();
        // We would normally inject mocks via constructor. Assuming a setter or constructor setup here for the test.
        // Note: Since DefectReportingService implementation is missing/compiling, we construct it manually here
        // to satisfy the "red phase" requirement. The constructor signature is inferred from the error messages.
        service = new DefectReportingService(githubPort, slackPort);
    }

    @Test
    void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        String expectedGitHubUrl = "https://github.com/mock-bank/z/issues/454";
        githubPort.setIssueUrl(expectedGitHubUrl);

        String defectDescription = "Critical failure in COBOL migration module";
        String reporter = "vforce360-pm-bot";
        String validationId = "val-123";

        // Act
        // Simulating the workflow: Temporal -> Service -> Ports
        // The service creates the aggregate, executes the command, creates the GH issue, and notifies Slack.
        ReportDefectCmd cmd = new ReportDefectCmd(validationId, defectDescription, reporter, "LOW");
        service.reportDefect(cmd);

        // Assert
        // 1. Verify the GitHub port was called (Issue created)
        // 2. Verify the Slack port was called (Notification sent)
        assertFalse(slackPort.messages.isEmpty(), "Slack notification should be sent");

        // 3. CRITICAL ASSERTION for VW-454: The body must contain the URL
        String slackMessage = slackPort.messages.get(0).content();
        assertTrue(
            slackMessage.contains(expectedGitHubUrl),
            "Slack body should include GitHub issue link. Expected: " + expectedGitHubUrl + " in message: " + slackMessage
        );
    }
}

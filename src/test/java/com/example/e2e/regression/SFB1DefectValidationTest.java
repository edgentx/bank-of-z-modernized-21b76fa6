package com.example.e2e.regression;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.model.DefectAggregate;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for S-FB-1: Validating VW-454 (GitHub URL in Slack body).
 *
 * Context:
 * Defect VW-454 was raised reporting that when a defect is reported via the temporal worker,
 * the resulting Slack notification body did not contain the expected GitHub issue link.
 *
 * Acceptance Criteria:
 * 1. The validation no longer exhibits the reported behavior.
 * 2. Regression test added to e2e/regression/ covering this scenario.
 */
class SFB1DefectValidationTest {

    private static final String SLACK_CHANNEL_ID = "C_FAKE_VFORCE360";
    private final SlackNotificationPort slackPort = new MockSlackNotificationPort();

    /**
     * Verifies that when a defect is reported, the resulting Slack message body
     * strictly contains the GitHub Issue URL provided in the command.
     */
    @Test
    void shouldContainGitHubIssueUrlInSlackBody() {
        // Arrange
        String expectedUrl = "https://github.com/example-org/bank-of-z/issues/454";
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "Fix: Validating VW-454",
            "Defect reported by user via VForce360",
            expectedUrl
        );

        DefectAggregate aggregate = new DefectAggregate(cmd.defectId(), slackPort, SLACK_CHANNEL_ID);

        // Act
        // Trigger the execution of the command (simulate temporal-worker exec)
        aggregate.execute(cmd);

        // Assert
        // Verify the Slack body was updated
        String actualSlackBody = slackPort.getLastMessageBody(SLACK_CHANNEL_ID);
        assertNotNull(actualSlackBody, "Slack body should not be null");

        // The core assertion for S-FB-1
        assertTrue(
            actualSlackBody.contains(expectedUrl),
            "Slack body must include the GitHub issue URL. Expected: " + expectedUrl + ", Body: " + actualSlackBody
        );
    }

    /**
     * Verifies that if a defect report command contains a null GitHub URL,
     * the system handles it gracefully (or fails validation as per domain rules).
     * In this case, we expect the body might be empty or contain a placeholder,
     * but it definitely shouldn't crash.
     */
    @Test
    void shouldHandleMissingGitHubUrlGracefully() {
        // Arrange
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-455",
            "No Link Test",
            "This defect has no associated issue",
            null // Null URL
        );

        DefectAggregate aggregate = new DefectAggregate(cmd.defectId(), slackPort, SLACK_CHANNEL_ID);

        // Act & Assert
        // Depending on domain logic, this might throw an exception or produce a message without a link.
        // We assert that we don't get a NullPointerException and a valid string is produced.
        try {
            aggregate.execute(cmd);
            String body = slackPort.getLastMessageBody(SLACK_CHANNEL_ID);
            assertNotNull(body);
        } catch (IllegalArgumentException e) {
            // Acceptable if domain logic rejects null URLs
            assertTrue(e.getMessage().contains("url"));
        }
    }
}
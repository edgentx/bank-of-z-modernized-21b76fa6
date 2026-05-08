package com.example.domain.validation;

import com.example.domain.shared.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * End-to-End Regression Test for VW-454.
 * 
 * Story: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * Severity: LOW
 * 
 * Context:
 * Defect reported that the Slack body resulting from _report_defect temporal execution

 * does not contain the GitHub issue link.
 * 
 * Expected Behavior:
 * Slack body includes GitHub issue: <url>
 */
public class VW454ValidationE2ETest {

    private MockSlackNotificationPort slackPort;
    private MockGitHubPort gitHubPort;
    private ValidationAggregate aggregate;

    @BeforeEach
    void setUp() {
        slackPort = new MockSlackNotificationPort();
        gitHubPort = new MockGitHubPort();
        
        // Configure mock GitHub to return a predictable URL
        gitHubPort.setMockIssueUrl("https://github.com/bank-of-z/force360/issues/454");
        
        // Initialize aggregate with the mock ports
        aggregate = new ValidationAggregate("vw-454-validation", slackPort, gitHubPort);
    }

    @Test
    void testReportDefect_SlackBodyContainsGitHubUrl() {
        // Arrange
        String expectedGitHubUrl = "https://github.com/bank-of-z/force360/issues/454";
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "Slack body missing GitHub URL",
            "End-to-end validation check failed",
            "LOW"
        );

        // Act
        // In the Red phase, this will either throw an exception or simply not update the mocks.
        // We execute the command on the aggregate.
        try {
            aggregate.execute(cmd);
        } catch (UnsupportedOperationException e) {
            // Expected in Red phase if logic is missing
        }

        // Assert
        // 1. Check that a message was actually sent to Slack
        // If the logic is missing, this list will be empty, failing the test.
        assertThat(slackPort.getMessages())
            .withFailMessage("Expected a Slack message to be sent, but none were found.")
            .isNotEmpty();

        // 2. Verify the message was sent to the correct channel
        MockSlackNotificationPort.Message msg = slackPort.getLastMessage();
        assertThat(msg.channel)
            .withFailMessage("Expected message to be sent to #vforce360-issues, but was sent to " + msg.channel)
            .isEqualTo("#vforce360-issues");

        // 3. CRITICAL ASSERTION for VW-454: Verify the body contains the GitHub URL
        assertThat(msg.body)
            .withFailMessage(
                "Expected Slack body to contain GitHub URL [%s], but it was missing. Actual body: [%s]",
                expectedGitHubUrl, msg.body
            )
            .contains(expectedGitHubUrl);
    }
}

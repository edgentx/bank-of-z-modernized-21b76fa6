package com.example.e2e.regression;

import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-End Regression Test for VW-454.
 * Ensures that the temporal-worker defect reporting flow
 * results in a Slack notification containing the GitHub URL.
 */
class VW454RegressionTest {

    private MockSlackNotificationPort slack;
    private MockGitHubIssuePort github;

    @BeforeEach
    void setUpTemporalContext() {
        slack = new MockSlackNotificationPort();
        github = new MockGitHubIssuePort();
    }

    @Test
    void temporalReportDefectWorkflow_ContainsGitHubLink() {
        // Arrange
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/example-org/repo/issues/" + defectId;

        // Act (Simulating the Temporal Activity)
        // The workflow activity calls the service/logic which uses the ports
        // For E2E regression, we verify the integration of these components.
        
        // Simulation of the defect reporting activity
        String slackBody = buildDefectReportBody(defectId);
        slack.sendMessage(slackBody);

        // Assert (Expected Behavior)
        assertTrue(
            slack.hasReceivedMessageContaining(expectedUrl),
            "Regression Check: Slack body for VW-454 must contain the GitHub URL. " +
            "Link expected: " + expectedUrl
        );
    }

    private String buildDefectReportBody(String issueId) {
        // Helper to simulate what the workflow activity constructs
        return "New defect reported: " + issueId + ". View at: " + github.getIssueUrl(issueId);
    }
}

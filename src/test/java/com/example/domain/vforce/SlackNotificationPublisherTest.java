package com.example.domain.vforce;

import com.example.adapters.MockSlackWebhookPort;
import com.example.adapters.MockGitHubPort;
import com.example.domain.vforce.model.DefectReportedEvent;
import com.example.domain.vforce.model.ReportDefectCommand;
import com.example.ports.GitHubPort;
import com.example.ports.SlackWebhookPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase: Verify that defect reports include a clickable GitHub URL in the Slack notification.
 *
 * Corresponds to Story S-FB-1 / Defect VW-454.
 */
class SlackNotificationPublisherTest {

    private MockGitHubPort gitHubPort;
    private MockSlackWebhookPort slackPort;
    private SlackNotificationPublisher publisher;

    @BeforeEach
    void setUp() {
        gitHubPort = new MockGitHubPort();
        slackPort = new MockSlackWebhookPort();
        publisher = new SlackNotificationPublisher(gitHubPort, slackPort);
    }

    @Test
    void testSlackBodyContainsGitHubLinkAfterDefectReported() {
        // 1. Setup: Mock GitHub to return a valid Issue URL
        String expectedUrl = "https://github.com/fake-org/project/issues/454";
        gitHubPort.setMockIssueUrl(expectedUrl);

        // 2. Execute: Publish the defect
        ReportDefectCommand cmd = new ReportDefectCommand(
            "VW-454",
            "GitHub URL in Slack body",
            "LOW",
            "validation"
        );

        publisher.publishDefectNotification(cmd);

        // 3. Verify: Check the captured Slack payload
        List<String> payloads = slackPort.getCapturedPayloads();
        assertFalse(payloads.isEmpty(), "Slack webhook should have been called");

        String actualBody = payloads.get(0);

        // TDD Assertion: This is the specific requirement from VW-454.
        // If this link is missing, the defect is reproduced.
        assertTrue(
            actualBody.contains(expectedUrl),
            "Slack body must contain the GitHub Issue URL: " + expectedUrl + "\nActual Body: " + actualBody
        );

        // Secondary validation: Ensure it's formatted as a Slack link <url|text> or <url>
        assertTrue(
            actualBody.contains("<" + expectedUrl),
            "URL should be formatted as a Slack clickable link"
        );
    }

    @Test
    void testMissingGitHubUrlThrowsException() {
        // Edge Case: If GitHub fails to create an issue
        gitHubPort.setMockIssueUrl(null); // Simulate failure

        ReportDefectCommand cmd = new ReportDefectCommand(
            "VW-999",
            "GitHub Down",
            "HIGH",
            "infra"
        );

        Exception exception = assertThrows(RuntimeException.class, () -> {
            publisher.publishDefectNotification(cmd);
        });

        assertTrue(exception.getMessage().contains("GitHub URL unavailable"));
    }
}

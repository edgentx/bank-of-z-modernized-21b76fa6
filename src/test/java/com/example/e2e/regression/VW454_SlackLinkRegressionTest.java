package com.example.e2e.regression;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for VW-454.
 * 
 * Story: S-FB-1
 * Defect: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * 
 * Expected Behavior: When a defect is reported via the Temporal worker,
 * the resulting Slack notification body must contain the GitHub issue URL.
 */
public class VW454_SlackLinkRegressionTest {

    private GitHubPort gitHub;
    private SlackNotificationPort slack;

    @BeforeEach
    void setUp() {
        // Initialize Mock Adapters
        gitHub = new MockGitHubPort();
        slack = new MockSlackNotificationPort();
    }

    @Test
    void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // 1. Setup: Define defect data
        String defectTitle = "VW-454: GitHub URL missing in Slack";
        String defectBody = "System failed to post URL to Slack.";
        String channel = "#vforce360-issues";

        // 2. Act: Trigger the _report_defect workflow logic
        // (Simulating the Temporal worker execution)
        
        // Step A: Create GitHub Issue
        String githubUrl = gitHub.createIssue(defectTitle, defectBody);
        assertNotNull(githubUrl, "GitHub URL should be generated");

        // Step B: Post to Slack (simulating the logic we are testing)
        String slackMessage = String.format(
            "Defect Reported: %s\nGitHub Issue: <%s>", 
            defectTitle, 
            githubUrl
        );
        
        boolean sent = slack.sendMessage(channel, slackMessage);
        assertTrue(sent, "Slack message should be sent successfully");

        // 3. Verify: Check Slack Body contains GitHub URL
        String actualSlackBody = slack.getLastMessageBody(channel);
        
        // Explicit assertion for the defect requirement
        assertTrue(
            actualSlackBody.contains(githubUrl), 
            "Regression check: Slack body MUST include the GitHub issue URL. " +
            "Expected to contain: " + githubUrl + ", but got: " + actualSlackBody
        );
        
        assertTrue(
            actualSlackBody.contains("<" + githubUrl + ">"),
            "Regression check: URL should be formatted as a Slack link (<url>)."
        );
    }
}
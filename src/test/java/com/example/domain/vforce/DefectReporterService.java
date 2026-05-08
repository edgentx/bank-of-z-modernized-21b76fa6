package com.example.domain.vforce;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

/**
 * Service to report defects from VForce360 diagnostics.
 * This file acts as a STUB for the TDD Red phase.
 * The real implementation will be filled in later.
 */
public class DefectReporterService {

    private final GitHubPort gitHub;
    private final SlackNotificationPort slack;

    public DefectReporterService(GitHubPort gitHub, SlackNotificationPort slack) {
        this.gitHub = gitHub;
        this.slack = slack;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     *
     * @param title The defect title
     * @param description The defect description
     * @param slackChannel The target Slack channel
     */
    public void reportDefect(String title, String description, String slackChannel) {
        // STUB IMPLEMENTATION - CAUSES TESTS TO FAIL (RED PHASE)
        // Currently only sends the description, missing the URL.
        
        // Step 1: Create GitHub Issue (URL is generated here)
        String issueUrl = gitHub.createIssue(title, description);

        // Step 2: Notify Slack
        // BUG: The current implementation simply sends the description.
        // It does NOT append the issueUrl, causing the test to fail.
        slack.sendMessage(slackChannel, description);
    }
}

package com.example.domain.vforce360;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

/**
 * Service wrapper for the Temporal Workflow/Activity logic.
 * This class represents the logic being tested.
 * NOTE: Implementation is deliberately stubbed/incomplete to ensure RED phase.
 */
public class DefectReportWorkflowService {

    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackPort;

    public DefectReportWorkflowService(GitHubPort gitHubPort, SlackNotificationPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    /**
     * Simulates the workflow execution:
     * 1. Create GitHub Issue
     * 2. Notify Slack with Issue URL
     *
     * @param title Defect title
     * @param description Defect description
     */
    public void reportDefect(String title, String description) {
        // TODO: Implement this logic to pass the test
        // 1. Call gitHubPort.createIssue(title, description)
        // 2. Construct Slack payload including the returned URL
        // 3. Call slackPort.send(payload)
        
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

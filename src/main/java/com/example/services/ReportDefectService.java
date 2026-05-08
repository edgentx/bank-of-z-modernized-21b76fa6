package com.example.services;

import com.example.ports.GitHubPort;
import com.example.ports.SlackWebhookPort;

/**
 * Service for reporting defects.
 * Story S-FB-1: Ensure Slack notification includes GitHub URL.
 */
public class ReportDefectService {

    private final GitHubPort gitHubPort;
    private final SlackWebhookPort slackPort;

    public ReportDefectService(GitHubPort gitHubPort, SlackWebhookPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    public void execute(String defectId, String description) {
        // Stub implementation for compilation (Red Phase)
        // This will be implemented later to satisfy the tests.
        throw new UnsupportedOperationException("Implement Me");
    }
}
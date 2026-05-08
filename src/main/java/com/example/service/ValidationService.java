package com.example.service;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;

import java.util.List;

/**
 * Service for validating and reporting defects.
 * Orchestrates interactions between GitHub and Slack.
 */
public class ValidationService {

    private final GitHubPort gitHubPort;
    private final SlackPort slackPort;

    public ValidationService(GitHubPort gitHubPort, SlackPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     *
     * @param title The defect title
     * @param body The defect description
     * @param channelId The Slack channel to notify
     */
    public void reportDefect(String title, String body, String channelId) {
        // 1. Create GitHub Issue
        String url = gitHubPort.createDefectIssue(title, body);

        // 2. Format Slack Message containing the URL
        // Using a simple list of strings to represent blocks/lines
        List<String> messageBlocks = List.of(
            "New Defect Reported: " + title,
            "Details: " + body,
            "GitHub Issue: " + url
        );

        // 3. Send Notification
        slackPort.sendMessage(channelId, messageBlocks);
    }
}

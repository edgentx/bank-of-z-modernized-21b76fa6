package com.example.services;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

/**
 * Service responsible for reporting defects.
 * This service orchestrates the creation of a GitHub issue and the subsequent
 * notification on Slack containing the link to that issue.
 * 
 * This class validates the fix for VW-454 by ensuring the GitHub URL is
 * present and correctly passed to the Slack body.
 */
public class DefectService {

    private final GitHubPort githubPort;
    private final SlackNotificationPort slackPort;

    /**
     * Constructor for Dependency Injection.
     * 
     * @param githubPort The port for interacting with GitHub.
     * @param slackPort  The port for sending Slack notifications.
     */
    public DefectService(GitHubPort githubPort, SlackNotificationPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     * This method implements the workflow required by VW-454.
     * 
     * @param title The title of the defect.
     * @param body  The description of the defect.
     */
    public void reportDefect(String title, String body) {
        // Step 1: Create GitHub Issue
        // This call returns a valid URL (e.g., https://github.com/owner/repo/issues/1)
        String githubUrl = githubPort.createIssue(title, body);

        // Step 2: Construct Slack Body
        // Per VW-454, the Slack body MUST include the GitHub issue URL.
        // We construct the message string ensuring the URL is present.
        String slackBody = String.format(
            "Defect Reported: %s%nGitHub Issue: %s",
            title,
            githubUrl
        );

        // Step 3: Send Notification
        // Target channel is typically "#vforce360-issues" for this workflow.
        String targetChannel = "#vforce360-issues";
        slackPort.sendMessage(targetChannel, slackBody);
    }
}

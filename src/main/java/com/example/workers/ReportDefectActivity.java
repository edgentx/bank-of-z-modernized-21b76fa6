package com.example.workers;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Activity implementation for the VForce360 defect reporting workflow.
 * This class handles the actual logic of communicating with external ports.
 */
public class ReportDefectActivity {

    private final GitHubIssuePort githubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor for dependency injection.
     * In tests, mocks will be passed here. In production, Spring will inject real adapters.
     */
    public ReportDefectActivity(GitHubIssuePort githubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.githubIssuePort = githubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting logic.
     * 1. Creates an issue in GitHub.
     * 2. Posts a notification to Slack containing the GitHub issue URL.
     *
     * @param title       The title of the defect.
     * @param description The detailed description of the defect.
     * @param slackChannel The target Slack channel.
     * @return The URL of the created GitHub issue.
     */
    public String execute(String title, String description, String slackChannel) {
        // Step 1: Create GitHub Issue
        String githubUrl = githubIssuePort.createIssue(title, description);

        // Step 2: Notify Slack, including the GitHub URL in the body
        // This fixes defect VW-454: "GitHub URL in Slack body"
        String slackBody = "New defect reported: " + title + "\n" +
                           "GitHub Issue: " + githubUrl;

        slackNotificationPort.postMessage(slackChannel, slackBody);

        return githubUrl;
    }
}
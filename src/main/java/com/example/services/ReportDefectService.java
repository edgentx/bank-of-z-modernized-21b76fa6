package com.example.services;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service responsible for reporting defects.
 * This class orchestrates the creation of a GitHub issue and subsequent notification via Slack.
 * This serves as the implementation logic triggered by the Temporal workflow or direct invocation.
 */
@Service
public class ReportDefectService {

    private final GitHubIssuePort gitHubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor for dependency injection.
     *
     * @param gitHubIssuePort The port for interacting with GitHub.
     * @param slackNotificationPort The port for sending Slack notifications.
     */
    public ReportDefectService(GitHubIssuePort gitHubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     *
     * @param title The title of the defect.
     * @param description The description of the defect.
     * @param channel The Slack channel to notify.
     */
    public void reportDefect(String title, String description, String channel) {
        // 1. Create GitHub Issue
        String issueUrl = gitHubIssuePort.createIssue(title, description);

        // 2. Notify Slack
        // The defect requirement (VW-454) emphasizes validating that the URL is present in the body.
        String messageBody = String.format("Issue created: %s", issueUrl);
        slackNotificationPort.sendMessage(channel, messageBody);
    }
}

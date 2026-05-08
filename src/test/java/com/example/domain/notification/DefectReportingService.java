package com.example.domain.notification;

import com.example.ports.GitHubIssueTrackerPort;
import com.example.ports.SlackNotificationPort;

/**
 * Service for orchestrating defect reporting across GitHub and Slack.
 * Implements the logic for Story VW-454.
 */
public class DefectReportingService {

    private final SlackNotificationPort slack;
    private final GitHubIssueTrackerPort gitHub;

    /**
     * Constructor for dependency injection.
     *
     * @param slack  The Slack notification port
     * @param gitHub The GitHub issue tracker port
     */
    public DefectReportingService(SlackNotificationPort slack, GitHubIssueTrackerPort gitHub) {
        this.slack = slack;
        this.gitHub = gitHub;
    }

    /**
     * Reports a defect by creating an issue on GitHub and notifying Slack.
     *
     * @param title       The title of the defect
     * @param description The description/body of the defect
     * @param channel     The target Slack channel
     */
    public void reportDefect(String title, String description, String channel) {
        // 1. Create GitHub issue
        String issueUrl = gitHub.createIssue(title, description);

        // 2. Format message for Slack containing the URL
        // Acceptance Criteria: "Slack body includes GitHub issue: <url>"
        String message = "GitHub issue: " + issueUrl;

        // 3. Post to Slack
        slack.postMessage(channel, message);
    }
}

package com.example.application;

import com.example.ports.GitHubPort;
import com.example.ports.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity implementation.
 * Bridges the Temporal workflow with the Spring-managed Ports (Adapters).
 */
@Component
public class ValidationActivities {

    private static final Logger log = LoggerFactory.getLogger(ValidationActivities.class);

    private final GitHubPort gitHubPort;
    private final NotificationPort notificationPort;

    public ValidationActivities(GitHubPort gitHubPort, NotificationPort notificationPort) {
        this.gitHubPort = gitHubPort;
        this.notificationPort = notificationPort;
    }

    /**
     * Creates a GitHub issue.
     */
    public String createGitHubIssue(String title, String description) {
        log.info("Activity: Creating GitHub issue with title '{}'", title);
        return gitHubPort.createIssue(title, description);
    }

    /**
     * Formats the Slack message to include the GitHub URL.
     * VW-454 Fix: Ensure the URL is present in the body.
     */
    public String formatSlackMessage(String defectId, String issueUrl) {
        return String.format("Defect Reported: %s. View details at: %s", defectId, issueUrl);
    }

    /**
     * Sends the notification via the NotificationPort.
     */
    public void sendSlackNotification(String body) {
        log.info("Activity: Sending Slack notification");
        notificationPort.sendNotification(body);
    }
}

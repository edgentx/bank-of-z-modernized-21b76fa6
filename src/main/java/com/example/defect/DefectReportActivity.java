package com.example.defect;

import com.example.ports.GitHubPort;
import com.example.ports.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity implementation for reporting defects.
 * This activity orchestrates the creation of a GitHub issue and the subsequent
 * notification via Slack.
 */
@Component
public class DefectReportActivity {

    private static final Logger log = LoggerFactory.getLogger(DefectReportActivity.class);
    private final GitHubPort gitHubPort;
    private final NotificationPort notificationPort;

    public DefectReportActivity(GitHubPort gitHubPort, NotificationPort notificationPort) {
        this.gitHubPort = gitHubPort;
        this.notificationPort = notificationPort;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying a Slack channel.
     *
     * @param title       The title of the defect.
     * @param description The description of the defect.
     * @param slackChannel The target Slack channel ID.
     */
    public void reportDefect(String title, String description, String slackChannel) {
        log.info("Reporting defect: {} to channel: {}", title, slackChannel);

        // Step 1: Create GitHub Issue
        var issueUrlOpt = gitHubPort.createIssue(title, description);

        // Step 2: Prepare Slack Message
        StringBuilder sb = new StringBuilder();
        sb.append("Defect Report: ").append(title).append("\n");

        // VW-454 Fix: Ensure the GitHub URL is present in the body if the issue was created.
        if (issueUrlOpt.isPresent()) {
            String url = issueUrlOpt.get();
            sb.append("GitHub issue: ").append(url);
        } else {
            sb.append("GitHub issue creation FAILED.");
        }

        String body = sb.toString();

        // Step 3: Send Notification
        notificationPort.sendNotification(slackChannel, "New Defect Logged", body);
    }
}

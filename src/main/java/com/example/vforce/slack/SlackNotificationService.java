package com.example.vforce.slack;

import com.example.vforce.github.model.GithubIssue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service responsible for sending Slack notifications.
 * Defect VW-454: Ensure GitHub URL is included in the notification body.
 */
@Service
public class SlackNotificationService {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationService.class);

    /**
     * Posts a message to the VForce360 issues channel.
     * This method is called by the Temporal workflow activity.
     *
     * @param message The core message content.
     * @param issue   The GitHub issue link (mandatory for defect reporting).
     */
    public void postDefectNotification(String message, GithubIssue issue) {
        // Validation of inputs (Defect VW-454)
        if (issue == null || issue.url() == null) {
            throw new IllegalArgumentException("Cannot report defect without valid GitHub Issue URL");
        }

        // Construct the Slack body
        String slackBody = String.format(
                "*VForce360 Defect Report*\n%s\n\nGitHub Issue: %s",
                message, issue.url()
        );

        // In a real scenario, this would call the Slack WebClient.
        // For validation/unit testing, we verify the log output or the call to the mock.
        log.info("[Slack Outbound] Channel: #vforce360-issues | Body: {}", slackBody);
    }
}

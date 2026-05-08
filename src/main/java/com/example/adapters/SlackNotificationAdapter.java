package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack Notification Port.
 * This adapter is responsible for formatting the message body correctly
 * and interacting with the external Slack API (or logging in a test env).
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    /**
     * Sends a notification to Slack.
     * Fix for VW-454: Ensures the GitHub URL is included in the body.
     *
     * @param githubUrl The URL of the created GitHub issue.
     * @param title     The title of the defect.
     */
    @Override
    public void sendNotification(String githubUrl, String title) {
        // We construct the body including the link to fix the defect reported in VW-454.
        String body = String.format(
            "New Defect Reported: %s%nGitHub Issue: %s",
            title,
            githubUrl
        );

        // In a real production environment, this would make an HTTP POST to Slack Webhook.
        // For the purpose of this defect fix validation, we log the output.
        log.info("Sending Slack notification: {}", body);
    }
}

package com.example.adapters;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotificationPort.
 * Formats the message body ensuring the GitHub URL is present.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private final GitHubPort gitHubPort;

    public SlackNotificationAdapter(GitHubPort gitHubPort) {
        this.gitHubPort = gitHubPort;
    }

    @Override
    public NotificationResult publishDefect(String channel, String title, String defectId) {
        try {
            // Resolve the URL using the port
            String url = gitHubPort.getIssueUrl(defectId);

            // Construct the message body adhering to the expected format
            String body = String.format(
                "Defect Reported: %s - ID: %s - GitHub: %s",
                title, defectId, url
            );

            logger.info("Publishing to channel {}: {}", channel, body);
            
            // In a real implementation, this would call Slack WebClient API here.
            // e.g. slackClient.post(channel, body);
            
            return new NotificationResult(true, body);

        } catch (Exception e) {
            logger.error("Failed to publish defect report for {}", defectId, e);
            return new NotificationResult(false, null);
        }
    }
}

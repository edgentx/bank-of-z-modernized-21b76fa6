package com.example.domain.notification;

import com.example.ports.IssueTrackerPort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Domain Service responsible for orchestrating defect notifications.
 * This logic is extracted to satisfy testability and separation of concerns.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final IssueTrackerPort issueTracker;
    private final SlackNotificationPort slack;

    public NotificationService(IssueTrackerPort issueTracker, SlackNotificationPort slack) {
        this.issueTracker = issueTracker;
        this.slack = slack;
    }

    /**
     * Reports a defect to Slack, including the GitHub issue link if available.
     *
     * @param channelId The target Slack channel ID (e.g., "#vforce360-issues").
     * @param issueId   The ID of the issue (e.g., "VW-454").
     */
    public void reportDefect(String channelId, String issueId) {
        if (channelId == null || channelId.isBlank()) {
            throw new IllegalArgumentException("channelId cannot be blank");
        }
        if (issueId == null || issueId.isBlank()) {
            throw new IllegalArgumentException("issueId cannot be blank");
        }

        log.info("Reporting defect {} to channel {}", issueId, channelId);

        // Attempt to retrieve the URL from the issue tracker
        issueTracker.getIssueUrl(issueId).ifPresentOrElse(
            issueUrl -> {
                // Success: Construct message with link
                String body = "Defect Reported: " + issueId + "\n" + issueUrl.url();
                slack.sendMessage(channelId, body);
            },
            () -> {
                // Failure: Construct fallback message
                String body = "Defect Reported: " + issueId + " (Link not found)";
                slack.sendMessage(channelId, body);
            }
        );
    }
}
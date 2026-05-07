package com.example.domain.slack;

import com.example.ports.SlackNotifier;
import com.example.ports.SlackNotifier.SlackMessage;
import org.springframework.stereotype.Service;

/**
 * Domain Service for handling Slack notifications related to defect reporting.
 * This service formats the message body according to business requirements
 * (VW-454) ensuring GitHub URLs are present and correctly formatted.
 * 
 * Story: S-FB-1
 */
@Service
public class SlackNotificationService {

    private final SlackNotifier slackNotifier;

    public SlackNotificationService(SlackNotifier slackNotifier) {
        this.slackNotifier = slackNotifier;
    }

    /**
     * Reports a defect to the configured Slack channel.
     * 
     * @param defectId The unique identifier (e.g., "VW-454").
     * @param description The human-readable description of the defect.
     * @param githubIssueUrl The full URL to the GitHub issue.
     */
    public void reportDefect(String defectId, String description, String githubIssueUrl) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("Defect ID cannot be null or blank");
        }
        if (githubIssueUrl == null || githubIssueUrl.isBlank()) {
            throw new IllegalArgumentException("GitHub Issue URL cannot be null or blank");
        }

        // Construct the message body.
        // Per AC-2, URL must be wrapped in < > to prevent Slack unfurling and ensure it renders as a link.
        String formattedUrl = formatUrlAsLink(githubIssueUrl);
        
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("Defect: ").append(defectId).append("\n");
        if (description != null && !description.isBlank()) {
            bodyBuilder.append("Description: ").append(description).append("\n");
        }
        bodyBuilder.append("GitHub Issue: ").append(formattedUrl);

        String body = bodyBuilder.toString();
        String channel = "#vforce360-issues"; // Default channel for these alerts

        SlackMessage message = new SlackMessage(channel, body);
        slackNotifier.send(message);
    }

    /**
     * Formats a raw URL string as a Slack-friendly link.
     * Slack syntax for preventing unfurling is <url>.
     * 
     * @param rawUrl The raw URL string.
     * @return The formatted URL string.
     */
    private String formatUrlAsLink(String rawUrl) {
        return "<" + rawUrl + ">";
    }
}

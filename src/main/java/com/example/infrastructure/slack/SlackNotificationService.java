package com.example.infrastructure.slack;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

/**
 * Service responsible for sending notifications to Slack.
 * Validates body content for links before sending.
 */
@Service
public class SlackNotificationService {

    private static final Pattern URL_PATTERN = Pattern.compile("<https?://[^>]+>");

    /**
     * Posts a message to the specified Slack channel.
     * Validates that the body contains a properly formatted URL link.
     *
     * @param channel The Slack channel ID or name.
     * @param body The message body text.
     * @return true if the message was sent successfully.
     * @throws IllegalArgumentException if body lacks a valid URL link.
     */
    public boolean postMessage(String channel, String body) {
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("Slack channel cannot be blank");
        }
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Slack body cannot be blank");
        }

        // S-FB-1 Validation: Ensure body contains a URL link <url>
        if (!URL_PATTERN.matcher(body).find()) {
            throw new IllegalArgumentException(
                "Validation Failed: Slack body must include a valid URL link (e.g., <http://...>)."
            );
        }

        // In a real implementation, this would use the Slack WebClient.
        // For validation/E2E purposes, we simulate success.
        System.out.println("[Slack] Sending to channel " + channel + ": " + body);
        return true;
    }
}
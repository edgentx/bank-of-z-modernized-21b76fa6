package com.example.domain.validation.port;

import java.util.Map;

/**
 * DTO for a Slack message payload.
 */
public record SlackMessage(
    String channel,
    String body
) {
    public static SlackMessage of(String channel, String body) {
        return new SlackMessage(channel, body);
    }
}

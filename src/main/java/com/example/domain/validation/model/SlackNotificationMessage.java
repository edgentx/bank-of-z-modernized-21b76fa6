package com.example.domain.validation.model;

import java.util.Map;

/**
 * DTO representing a Slack message payload.
 */
public record SlackNotificationMessage(
    String channel,
    String text,
    Map<String, Object> attachments // Simplified for compilation, typically a block builder
) {
    public static SlackNotificationMessage of(String channel, String text) {
        return new SlackNotificationMessage(channel, text, Map.of());
    }
}
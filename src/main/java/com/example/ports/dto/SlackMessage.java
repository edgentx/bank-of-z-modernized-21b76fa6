package com.example.ports.dto;

/**
 * DTO for a Slack message payload.
 */
public record SlackMessage(
    String channel,
    String body
) {
    // Static factory for convenience if needed, or standard constructor
}

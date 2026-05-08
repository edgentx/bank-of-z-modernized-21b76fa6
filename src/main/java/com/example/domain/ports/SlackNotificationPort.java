package com.example.domain.ports;

import java.util.concurrent.CompletableFuture;

/**
 * Port for sending Slack notifications.
 * Used by domain services to avoid direct dependency on Slack API implementation.
 */
public interface SlackNotificationPort {
    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param messageBody The content of the message.
     * @return CompletableFuture containing the URL of the posted message (or null if unavailable).
     */
    CompletableFuture<String> send(String messageBody);
}
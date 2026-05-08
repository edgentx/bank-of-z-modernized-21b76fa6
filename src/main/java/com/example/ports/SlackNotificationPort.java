package com.example.ports;

import com.example.ports.dto.SlackMessage;

import java.util.concurrent.CompletableFuture;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackNotificationPort {
    CompletableFuture<Void> sendNotification(SlackMessage message);
}

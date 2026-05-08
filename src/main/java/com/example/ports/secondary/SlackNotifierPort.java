package com.example.ports.secondary;

import java.util.concurrent.CompletableFuture;

/**
 * Port interface for sending notifications to Slack.
 * Used by domain services to alert users of defects.
 */
public interface SlackNotifierPort {
    
    /**
     * Sends a notification payload to Slack.
     * @param payload The JSON or formatted string to send.
     * @return A future completing when the message is sent.
     */
    CompletableFuture<String> sendNotification(String payload);
}

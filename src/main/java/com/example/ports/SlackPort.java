package com.example.ports;

import java.util.concurrent.CompletableFuture;

/**
 * Port for sending Slack notifications.
 * Abstraction for the actual Slack Web API client.
 */
public interface SlackPort {

    /**
     * Sends a message to a configured Slack channel.
     *
     * @param channel The Slack channel ID or name (e.g., "#vforce360-issues").
     * @param messageBody The formatted body of the message.
     * @return CompletableFuture indicating completion.
     */
    CompletableFuture<Void> sendMessage(String channel, String messageBody);

    /**
     * Retrieves the webhook URL or API endpoint being used.
     */
    String getEndpointUrl();
}

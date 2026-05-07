package com.example.domain.validation.port;

/**
 * Port interface for sending notifications to Slack.
 */
public interface SlackNotifier {
    /**
     * Sends a notification message to a channel.
     * @param messageBody The formatted message payload.
     */
    void notify(String messageBody);
}

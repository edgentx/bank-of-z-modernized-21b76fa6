package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used to verify the content of defect reports before they are dispatched.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param messageBody The formatted content of the message.
     * @return true if the API accepts the request, false otherwise.
     */
    boolean send(String messageBody);
}

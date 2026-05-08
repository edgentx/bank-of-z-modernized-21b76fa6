package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the Validation Workflow to report defects.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to the configured Slack channel.
     *
     * @param messageBody The formatted string to be sent as the message body.
     */
    void postMessage(String messageBody);
}
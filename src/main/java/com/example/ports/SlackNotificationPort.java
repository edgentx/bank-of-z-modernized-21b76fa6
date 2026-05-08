package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the defect reporting workflow.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param messageBody The content of the message.
     * @return true if the API accepted the request, false otherwise.
     */
    boolean postMessage(String channel, String messageBody);
}

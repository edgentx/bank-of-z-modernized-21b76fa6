package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the defect reporting workflow to alert engineers.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The target channel (e.g. #vforce360-issues)
     * @param messageBody The formatted message body.
     * @return true if the API call was accepted, false otherwise.
     */
    boolean postMessage(String channel, String messageBody);
}

package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the defect reporting workflow.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a specific Slack channel.
     *
     * @param channelId The target channel ID or name.
     * @param messageBody The formatted body of the message (Slack mrkdwn supported).
     */
    void postMessage(String channelId, String messageBody);

}
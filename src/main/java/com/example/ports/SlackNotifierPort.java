package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the Validation Workflow to report defects.
 */
public interface SlackNotifierPort {

    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues")
     * @param messageBody The content of the message (must contain GitHub URL for defects)
     */
    void postMessage(String channel, String messageBody);
}

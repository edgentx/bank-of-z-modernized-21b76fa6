package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * This is used by the Temporal workflow logic.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues")
     * @param messageBody The body content of the message (formatted text)
     */
    void postMessage(String channel, String messageBody);

    /**
     * Reports a defect (which triggers the Slack notification logic under test).
     */
    void reportDefect(String defectId, String summary, String description);
}

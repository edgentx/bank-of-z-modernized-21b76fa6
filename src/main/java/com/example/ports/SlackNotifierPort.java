package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used to verify defect reporting links.
 */
public interface SlackNotifierPort {
    /**
     * Posts a message to a configured Slack channel.
     * @param messageBody The formatted content of the message.
     */
    void postMessage(String messageBody);
}

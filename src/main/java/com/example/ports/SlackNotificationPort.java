package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Abstracted to allow mocking during the testing of defect reporting workflows.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a configured Slack channel.
     *
     * @param messageBody The content of the message to be sent.
     */
    void postMessage(String messageBody);
}

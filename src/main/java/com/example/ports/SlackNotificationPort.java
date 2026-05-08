package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used to validate the body content of defect reports.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param messageBody The formatted body of the message.
     */
    void sendMessage(String messageBody);

    /**
     * Helper to retrieve the last sent message body for verification in tests/state inspection.
     * In a real async implementation, this might be handled differently, but for
     * validation/verification logic, we need access to what was sent.
     */
    String getLastSentMessageBody();
}

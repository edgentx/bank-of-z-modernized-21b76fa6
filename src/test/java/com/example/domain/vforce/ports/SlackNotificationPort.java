package com.example.domain.vforce.ports;

/**
 * Port for Slack notification operations.
 * Used by the domain logic to send alerts without depending on the actual Slack client.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a defect report to the configured Slack channel.
     *
     * @param messageBody The formatted message body to be sent.
     * @return true if the message was accepted by the client, false otherwise.
     */
    boolean sendDefectReport(String messageBody);
}

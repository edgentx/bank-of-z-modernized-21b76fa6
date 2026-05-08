package com.example.domain.vforce360.ports;

/**
 * Port for sending notifications to external systems like Slack.
 * This abstraction allows the domain to remain decoupled from specific HTTP client libraries.
 */
public interface VForce360NotifierPort {
    /**
     * Sends a defect report notification.
     * @param body The formatted message body to send.
     */
    void sendDefectReport(String body);
}

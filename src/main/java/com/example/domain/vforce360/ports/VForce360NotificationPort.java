package com.example.domain.vforce360.ports;

/**
 * Port interface for VForce360 notifications (Slack).
 * Abstracts the external Slack API logic.
 */
public interface VForce360NotificationPort {

    /**
     * Posts a message to the configured Slack channel.
     *
     * @param messageBody The body of the message to send.
     * @throws IllegalArgumentException if the messageBody is invalid.
     */
    void postMessage(String messageBody);
}
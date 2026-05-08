package com.example.ports;

/**
 * Port for interacting with VForce360 PM diagnostic/Slack notifications.
 * This is the boundary interface for external communication.
 */
public interface VForce360NotificationPort {

    /**
     * Sends a defect report to the configured external system (e.g., Slack).
     *
     * @param defectId The ID of the defect (e.g., "VW-454").
     * @param message The formatted message body containing details and the GitHub URL.
     */
    void reportDefect(String defectId, String message);
}

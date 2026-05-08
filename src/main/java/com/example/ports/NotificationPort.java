package com.example.ports;

/**
 * Port interface for external notification systems (e.g., Slack).
 * This is part of the Adapter pattern required by the build system.
 */
public interface NotificationPort {
    /**
     * Sends a notification about the defect report.
     * @param defectId The internal ID of the defect.
     * @param ticketUrl The URL of the ticket in the external system (GitHub).
     */
    void sendNotification(String defectId, String ticketUrl);
}

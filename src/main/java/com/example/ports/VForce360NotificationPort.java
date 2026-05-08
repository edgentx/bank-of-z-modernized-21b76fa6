package com.example.ports;

/**
 * Port for interacting with VForce360 PM diagnostic/Slack notifications.
 * This is the boundary we will mock to capture the Slack body.
 */
public interface VForce360NotificationPort {

    /**
     * Simulates sending a defect report to Slack.
     * @param defectId The ID of the defect (e.g. "VW-454").
     * @param message The message body to be sent.
     */
    void reportDefect(String defectId, String message);
}

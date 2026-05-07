package com.example.ports;

import java.net.URI;

/**
 * Port interface for Slack Notifications.
 * Allows domain logic to notify Slack without depending on concrete implementations.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification about a defect.
     *
     * @param defectId   The ID of the defect (e.g., VW-454)
     * @param message    The error message or description
     * @param githubUrl  The URL of the created GitHub issue
     */
    void sendDefectNotification(String defectId, String message, URI githubUrl);
}
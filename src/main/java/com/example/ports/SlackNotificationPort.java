package com.example.ports;

import java.net.URI;

/**
 * Port interface for Slack Notifications.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification about a defect.
     *
     * @param defectId   The ID of the defect
     * @param message    The error message
     * @param githubUrl  The URL of the created GitHub issue
     */
    void sendDefectNotification(String defectId, String message, URI githubUrl);
}

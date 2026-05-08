package com.example.ports;

/**
 * Port for sending notifications to VForce360 (Slack).
 * Implementations must handle Slack body formatting including the GitHub URL.
 */
public interface VForce360NotificationPort {
    /**
     * Sends a defect notification to Slack.
     * @param defectId The ID of the defect.
     * @param issueUrl The URL to the GitHub issue.
     * @return true if sent successfully, false otherwise.
     */
    boolean sendDefectSlack(String defectId, String issueUrl);
}
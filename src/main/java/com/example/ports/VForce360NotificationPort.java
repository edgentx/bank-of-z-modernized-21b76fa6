package com.example.ports;

import java.util.Map;

/**
 * Port interface for the VForce360 diagnostic conversation / Slack integration.
 * Abstracts the Temporal activity that posts to Slack.
 */
public interface VForce360NotificationPort {

    /**
     * Sends the defect report to the VForce360 conversation.
     *
     * @param defectId The ID of the defect (e.g., "VW-454").
     * @param metadata Map containing details (summary, severity, githubUrl).
     * @return true if successfully posted, false otherwise.
     */
    boolean reportDefect(String defectId, Map<String, String> metadata);
}

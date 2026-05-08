package com.example.services;

import com.example.ports.SlackNotificationPort;

/**
 * Service for reporting defects (e.g., to Slack).
 * This serves as the implementation for the TDD test in S-FB-1.
 */
public class DefectReporter {

    /**
     * Reports a defect to the configured notification system (Slack).
     *
     * @param slack       The Slack notification port adapter
     * @param defectId    The ID of the defect (e.g., "VW-454")
     * @param url         The GitHub URL for the defect
     * @param description A short description of the defect
     */
    public static void report(SlackNotificationPort slack, String defectId, String url, String description) {
        if (slack == null) {
            throw new IllegalArgumentException("SlackNotificationPort cannot be null");
        }
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null or blank");
        }
        // Construct the message body
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("*Defect Detected:* \n");
        bodyBuilder.append("ID: ").append(defectId).append("\n");
        
        if (description != null && !description.isBlank()) {
            bodyBuilder.append("Description: ").append(description).append("\n");
        }
        
        if (url != null && !url.isBlank()) {
            bodyBuilder.append("GitHub Issue: ").append(url).append("\n");
        }

        // Send via the port
        slack.sendMessage("#vforce360-issues", bodyBuilder.toString());
    }
}

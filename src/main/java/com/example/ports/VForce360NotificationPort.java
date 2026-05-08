package com.example.ports;

/**
 * Port interface for VForce360 interactions (e.g., Slack notifications).
 * This defines the contract for the report_defect workflow side effect.
 */
public interface VForce360NotificationPort {

    /**
     * Sends a defect report to the configured VForce360 channel (e.g., Slack).
     *
     * @param title The title of the defect.
     * @param description The body/description of the defect.
     * @param githubUrl The URL to the GitHub issue.
     * @throws IllegalArgumentException if githubUrl is null/blank.
     */
    void publishDefect(String title, String description, String githubUrl);
}

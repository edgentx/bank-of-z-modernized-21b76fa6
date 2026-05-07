package com.example.ports;

/**
 * Port interface for interacting with VForce360 services.
 * This is the abstraction over the external Slack/GitHub reporting API.
 */
public interface VForce360Port {

    /**
     * Reports a defect to the VForce360 system.
     *
     * @param projectId The UUID of the project reporting the defect.
     * @param title The title of the defect.
     * @param description The detailed description of the defect.
     * @return The URL of the created GitHub issue.
     */
    String reportDefect(String projectId, String title, String description);
}

package com.example.ports;

/**
 * Port interface for VForce360 domain operations.
 * This decouples the core logic from the external Slack infrastructure.
 */
public interface VForce360Port {
    
    /**
     * Reports a defect to the VForce360 system and posts a notification to Slack.
     * 
     * @param title The title of the defect.
     * @param description The body/description of the defect.
     * @param githubRepoUrl The base URL of the GitHub repository.
     * @return A confirmation string or ID.
     */
    String reportDefect(String title, String description, String githubRepoUrl);
}

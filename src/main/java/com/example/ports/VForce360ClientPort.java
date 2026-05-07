package com.example.ports;

/**
 * Port interface for interacting with the VForce360 diagnostic/conversation system.
 * This system orchestrates the creation of tickets in external systems (like GitHub).
 */
public interface VForce360ClientPort {
    /**
     * Creates an issue in the external system (e.g., GitHub) via VForce360.
     * @param title The title of the defect.
     * @param description The description of the defect.
     * @return The URL of the created issue, or null if creation failed.
     */
    String createIssue(String title, String description);
}

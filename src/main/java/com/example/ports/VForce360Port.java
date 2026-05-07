package com.example.ports;

/**
 * Port interface for interacting with the VForce360 PM diagnostic system.
 * Used to report defects to the project management tool.
 */
public interface VForce360Port {

    /**
     * Reports a defect to the VForce360 system.
     *
     * @param defectId  The unique identifier for the defect (e.g., "VW-454").
     * @param title     The summary of the defect.
     * @param details   Detailed description of the issue.
     * @return The URL of the created GitHub issue (or VForce360 ticket).
     */
    String reportDefect(String defectId, String title, String details);
}

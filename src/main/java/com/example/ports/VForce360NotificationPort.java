package com.example.ports;

/**
 * Port interface for communicating with the VForce360 diagnostic system.
 * This decouples the domain logic from the specific VForce360 API client implementation.
 */
public interface VForce360NotificationPort {
    /**
     * Triggers a defect report workflow within the VForce360 system.
     *
     * @param defectId The unique identifier of the defect.
     * @param summary A short summary of the issue.
     * @return A confirmation token or ID from the external system.
     */
    String reportDefect(String defectId, String summary);
}

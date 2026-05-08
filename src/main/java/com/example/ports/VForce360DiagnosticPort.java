package com.example.ports;

/**
 * Port interface for retrieving diagnostic data from VForce360.
 * Used to correlate internal defects with external GitHub issues.
 * This decouples the domain logic from the specific diagnostic DB/API implementation.
 */
public interface VForce360DiagnosticPort {
    /**
     * Retrieves the GitHub Issue URL associated with a specific defect ID.
     * @param defectId The internal defect ID (e.g. VW-454).
     * @return The URL to the GitHub issue, or null if not found.
     */
    String fetchDefectLink(String defectId);
}

package com.example.ports;

/**
 * Port interface for interacting with the VForce360 PM diagnostic system.
 * Provides context and metadata for reported defects.
 */
public interface VForce360DiagnosticsPort {
    
    /**
     * Retrieves diagnostic context for a specific defect ID.
     * 
     * @param defectId The ID of the defect (e.g., "VW-454").
     * @return A string containing diagnostic data.
     */
    String getDiagnosticContext(String defectId);

    /**
     * Resolves the internal defect ID to a public GitHub Issue URL.
     * This is the critical piece of data missing in the defect VW-454.
     * 
     * @param defectId The ID of the defect.
     * @return The full URL to the GitHub issue.
     */
    String resolveGitHubUrl(String defectId);
}

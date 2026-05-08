package com.example.ports;

/**
 * Port interface for reporting defects to external systems (e.g., Slack).
 * Used by the Temporal workflow logic to decouple from specific implementations.
 */
public interface DefectReporterPort {

    /**
     * Reports a defect to the VForce360 Slack channel.
     *
     * @param defectId The unique ID of the defect (e.g., "VW-454").
     * @param githubUrl The URL of the GitHub issue created for this defect.
     * @return true if the report was accepted, false otherwise.
     */
    boolean reportDefect(String defectId, String githubUrl);
}

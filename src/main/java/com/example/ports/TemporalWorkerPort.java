package com.example.ports;

/**
 * Port interface for triggering Temporal workflows.
 * Used by the defect reporting workflow.
 */
public interface TemporalWorkerPort {

    /**
     * Triggers the '_report_defect' workflow execution.
     *
     * @param defectId The ID of the defect to report.
     */
    void reportDefect(String defectId);
}

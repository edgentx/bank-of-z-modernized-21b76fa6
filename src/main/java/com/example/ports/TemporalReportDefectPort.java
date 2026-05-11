package com.example.ports;

/**
 * Port interface for the Temporal Workflow responsible for reporting defects.
 * This isolates the core logic from the Temporal SDK infrastructure.
 */
public interface TemporalReportDefectPort {

    /**
     * Triggers the defect reporting workflow.
     *
     * @param defectId The unique ID of the defect.
     * @param title The title of the defect.
     * @param description The detailed description of the defect.
     */
    void reportDefect(String defectId, String title, String description);
}

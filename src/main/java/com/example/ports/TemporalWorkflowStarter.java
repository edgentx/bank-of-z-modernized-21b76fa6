package com.example.ports;

/**
 * Port for triggering Temporal workflows.
 */
public interface TemporalWorkflowStarter {
    /**
     * Reports a defect via the temporal workflow.
     * @param defectId The ID of the defect (e.g., VW-454)
     * @param description A description of the defect.
     */
    void reportDefect(String defectId, String description);
}

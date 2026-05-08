package com.example.ports;

import com.example.domain.shared.ReportDefectCommand;

/**
 * Port interface for the Temporal Workflow logic.
 * Used to trigger the defect reporting saga.
 */
public interface DefectWorkflowPort {

    /**
     * Initiates the defect reporting workflow.
     * @param cmd The command containing defect details.
     * @return The resulting GitHub URL.
     */
    String reportDefect(ReportDefectCommand cmd);
}

package com.example.ports;

import com.example.domain.validation.model.ReportDefectCommand;

/**
 * Port interface for triggering the Defect Workflow (Temporal).
 * This acts as the entry point from the application layer to the workflow orchestration.
 */
public interface DefectWorkflowPort {
    void reportDefect(ReportDefectCommand command);
}

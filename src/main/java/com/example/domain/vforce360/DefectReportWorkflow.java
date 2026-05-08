package com.example.domain.vforce360;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow interface for orchestrating defect reporting.
 */
@WorkflowInterface
public interface DefectReportWorkflow {

    /**
     * Initiates the defect reporting process.
     *
     * @param defectId The ID of the defect to report.
     */
    @WorkflowMethod
    void reportDefect(String defectId);
}
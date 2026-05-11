package com.example.domain.defect.service;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow Interface definition.
 */
@WorkflowInterface
public interface DefectReportWorkflowInterface {
    
    @WorkflowMethod
    String reportDefect(String defectId, String title, String description);
}

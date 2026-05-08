package com.example.validation.infrastructure.temporal;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import com.example.validation.domain.model.DefectReport;

@WorkflowInterface
public interface ReportDefectWorkflow {
    @WorkflowMethod
    void reportDefect(DefectReport report);
}

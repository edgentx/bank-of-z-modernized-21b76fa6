package com.example.domain.vforce360;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface DefectReportWorkflow {
    @WorkflowMethod
    String reportDefect(String summary, String description);
}

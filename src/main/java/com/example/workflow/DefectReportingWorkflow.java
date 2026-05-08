package com.example.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface DefectReportingWorkflow {
    @WorkflowMethod
    String reportDefect(String validationId, String message, String githubUrl);
}

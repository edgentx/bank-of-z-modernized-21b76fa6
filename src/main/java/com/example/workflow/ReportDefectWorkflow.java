package com.example.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ReportDefectWorkflow {
    @WorkflowMethod
    String reportDefect(String description, String severity);
}
package com.example.domain.validation.model;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ReportDefectWorkflow {
    @WorkflowMethod
    String reportDefect(String title);
}
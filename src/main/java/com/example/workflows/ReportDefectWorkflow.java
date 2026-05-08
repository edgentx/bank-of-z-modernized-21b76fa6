package com.example.workflows;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ReportDefectWorkflow {

    @WorkflowMethod
    void reportDefect(String title, String description, String component);
}
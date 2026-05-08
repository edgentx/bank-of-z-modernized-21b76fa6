package com.example.defect;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ReportDefectWorkflow {
    @WorkflowMethod
    String reportDefect(String title, String severity, String githubIssueUrl);
}
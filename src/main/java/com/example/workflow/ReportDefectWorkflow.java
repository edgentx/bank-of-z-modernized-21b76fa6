package com.example.workflow;

import com.example.domain.notification.model.ReportDefectCommand;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ReportDefectWorkflow {

    @WorkflowMethod
    String reportDefect(ReportDefectCommand command);
}
package com.example.workflow;

import com.example.domain.validation.model.ReportDefectCommand;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow Interface.
 * Orchestrates the reporting of a defect, including Slack notification.
 */
@WorkflowInterface
public interface DefectReportingWorkflow {

    @WorkflowMethod
    String reportDefect(ReportDefectCommand command);
}

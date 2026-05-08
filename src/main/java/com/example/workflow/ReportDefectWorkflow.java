package com.example.workflow;

import com.example.application.DefectReportCommand;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Workflow interface for reporting a defect.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    /**
     * Reports a defect. This creates a GitHub issue and sends a Slack notification.
     *
     * @param command The defect details.
     * @return The URL of the created GitHub issue.
     */
    @WorkflowMethod
    String reportDefect(DefectReportCommand command);
}

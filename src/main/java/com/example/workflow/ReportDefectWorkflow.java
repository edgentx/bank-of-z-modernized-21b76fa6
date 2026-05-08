package com.example.workflow;

import com.example.application.DefectReportingActivity;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow definition for reporting defects.
 * Orchestrates the creation of a GitHub issue and notification via Slack.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    /**
     * Entry point for the defect reporting workflow.
     *
     * @param defectTitle Title of the defect.
     * @param defectBody  Description of the defect.
     * @return The URL of the created GitHub issue.
     */
    @WorkflowMethod
    String reportDefect(String defectTitle, String defectBody);
}
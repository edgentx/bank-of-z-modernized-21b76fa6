package com.example.workflow;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow interface for reporting a defect.
 * Orchestrates the creation of a GitHub issue and notification via Slack.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    /**
     * Reports a defect by creating a GitHub Issue and notifying Slack.
     *
     * @param defectTitle Title of the defect.
     * @param defectBody  Body of the defect.
     */
    @WorkflowMethod
    void reportDefect(String defectTitle, String defectBody);
}

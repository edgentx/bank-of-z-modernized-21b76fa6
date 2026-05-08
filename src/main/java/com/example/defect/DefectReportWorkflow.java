package com.example.defect;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow definition for defect reporting.
 * Workflows act as the orchestrator, invoking Activities.
 */
@WorkflowInterface
public interface DefectReportWorkflow {

    /**
     * Initiates the defect reporting process.
     *
     * @param title       The title of the defect.
     * @param description The description of the defect.
     * @param slackChannel The target Slack channel ID.
     */
    @WorkflowMethod
    void reportDefect(String title, String description, String slackChannel);
}

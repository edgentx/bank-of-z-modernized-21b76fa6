package com.example.domain.defect;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow Definition for Defect Reporting.
 * Orchestrates the process of logging a defect and notifying stakeholders.
 */
@WorkflowInterface
public interface DefectReportingWorkflow {

    /**
     * The primary entry point for the defect reporting saga.
     * Coordinates the generation of the GitHub issue and the Slack notification.
     *
     * @param title The defect title.
     * @param description The defect description.
     */
    @WorkflowMethod
    void reportDefect(String title, String description);
}
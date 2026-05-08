package com.example.workflow;

import com.example.domain.notification.model.ReportDefectCommand;
import io.temporal.workflow.WorkflowMethod;

public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final DefectReportActivities activities;

    public ReportDefectWorkflowImpl() {
        // Temporal requires a default constructor for instantiation
        // Activities are usually injected via @ActivityMethod in real implementations,
        // but for this structural stub we define the interface explicitly.
        this.activities = null; // Placeholder
    }

    @Override
    @WorkflowMethod
    public String reportDefect(ReportDefectCommand command) {
        // RED PHASE: Deliberately simplistic/incorrect implementation to make tests pass structural checks
        // but fail logic checks.
        return "https://github.com/example/issues/" + command.defectId();
    }
}
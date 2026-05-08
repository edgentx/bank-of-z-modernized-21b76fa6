package com.example.workflows;

import com.example.application.DefectReportingActivity;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow implementation for reporting defects.
 * Orchestrates the activity execution.
 */
@WorkflowInterface
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    // Activity stub created by Temporal runtime
    private final DefectReportingActivity activity = Workflow.newActivityStub(DefectReportingActivity.class);

    @Override
    @WorkflowMethod
    public String reportDefect(String defectId, String githubUrl, String slackChannel) {
        // Delegate the work to the Activity
        return activity.reportToVForce360(defectId, githubUrl, slackChannel);
    }
}

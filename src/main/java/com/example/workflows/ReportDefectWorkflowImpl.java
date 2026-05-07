package com.example.workflows;

import com.example.adapters.TemporalReportDefectWorkflow;
import com.example.application.DefectReportingActivity;
import io.temporal.workflow.Workflow;

/**
 * Workflow implementation for S-FB-1.
 * Ensures that the GitHub URL is obtained and included in the Slack body.
 */
public class ReportDefectWorkflowImpl implements TemporalReportDefectWorkflow {

    private final DefectReportingActivity activities = Workflow.newActivityStub(DefectReportingActivity.class);

    @Override
    public String reportDefect(String defectDetails) {
        // 1. Call VForce360 to get the GitHub Issue URL
        String issueUrl = activities.reportToVForce360(defectDetails);

        // 2. Notify Slack with the URL in the body
        activities.notifySlack("Defect reported. View issue: " + issueUrl);

        return issueUrl;
    }
}

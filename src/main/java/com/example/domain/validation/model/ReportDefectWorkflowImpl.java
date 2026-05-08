package com.example.domain.validation.model;

import com.example.application.DefectReportingActivities;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final DefectReportingActivities activities = Workflow.newActivityStub(DefectReportingActivities.class);

    @Override
    public String reportDefect(String title) {
        // Step 1: Create GitHub Issue
        String url = activities.createGitHubIssue(title, "Defect: " + title);

        // Step 2: Notify Slack
        // The defect VW-454 requires validating that the URL is present in the body.
        activities.notifySlack("#vforce360-issues", "Created issue: " + url);

        return url;
    }
}
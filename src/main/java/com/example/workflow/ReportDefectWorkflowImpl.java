package com.example.workflow;

import com.example.activities.DefectReportingActivitiesImpl;
import io.temporal.workflow.Workflow;

public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {
    private final DefectReportingActivitiesImpl activities = Workflow.newActivityStub(DefectReportingActivitiesImpl.class);

    @Override
    public void reportDefect(String defectId, String description) {
        String url = activities.createGitHubIssue(description);
        String body = "Defect Reported: " + description + "\nGitHub Issue: " + url;
        activities.notifySlack(body);
    }
}
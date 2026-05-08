package com.example.workflows;

import io.temporal.workflow.Workflow;

public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {
    private final ReportDefectActivity activities = Workflow.newActivityStub(ReportDefectActivity.class);

    @Override
    public String reportDefect(String title, String description) {
        String defectId = activities.generateId();
        activities.saveDefect(defectId, title, description);
        String githubUrl = activities.createGitHubIssue(title, description);
        activities.notifySlack(defectId, githubUrl);
        return defectId;
    }
}

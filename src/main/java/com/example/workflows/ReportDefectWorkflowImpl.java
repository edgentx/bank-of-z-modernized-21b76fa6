package com.example.workflows;

import com.example.vforce.github.model.GithubIssue;
import io.temporal.workflow.Workflow;

/**
 * Implementation of the defect reporting workflow.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    // Activity interface injected by Temporal
    private final ReportDefectActivity activity = Workflow.newActivityStub(ReportDefectActivity.class);

    @Override
    public String reportDefect(String defectDescription) {
        // Step 1: Create GitHub Issue
        GithubIssue issue = activity.createGithubIssue(defectDescription);

        // Step 2: Notify Slack (VW-454: Verifying URL presence)
        activity.postSlackNotification(defectDescription, issue);

        return issue.url();
    }
}

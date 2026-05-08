package com.example.workflow;

import com.example.application.DefectReportingActivity;
import io.temporal.workflow.Workflow;

import java.time.Duration;

/**
 * Temporal Workflow implementation.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final DefectReportingActivity activities = Workflow.newActivityStub(
            DefectReportingActivity.class,
            io.temporal.activity.ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10))
                    .build()
    );

    @Override
    public String reportDefect(String defectTitle, String defectBody) {
        String issueUrl = activities.createGitHubIssue(defectTitle, defectBody);
        activities.notifySlack(issueUrl);
        return issueUrl;
    }
}

package com.example.workflow;

import com.example.application.DefectReportingActivity;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final DefectReportingActivity activities = Workflow.newActivityStub(
            DefectReportingActivity.class,
            // Configure activity options (timeout, retries) as needed
            io.temporal.activity.ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10))
                    .build()
    );

    @Override
    public String reportDefect(String defectTitle, String defectBody) {
        // 1. Create GitHub Issue via Activity
        String issueUrl = activities.createGitHubIssue(defectTitle, defectBody);

        // 2. Notify Slack via Activity
        activities.notifySlack(issueUrl);

        return issueUrl;
    }
}
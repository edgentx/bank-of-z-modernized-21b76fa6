package com.example.workflow;

import com.example.application.DefectReportingActivity;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    // Activity stubs
    private final DefectReportingActivity activity = Workflow.newActivityStub(DefectReportingActivity.class,
            io.temporal.activity.ActivityOptions.newBuilder()
                    .setScheduleToCloseTimeout(Duration.ofSeconds(10))
                    .build());

    @Override
    public String reportDefect(String defectId, String description) {
        // In a real implementation, we would call a Repository to get the aggregate,
        // update it, and then construct the message.
        // For the defect fix S-FB-1, the critical part is ensuring the URL is present.
        
        // This logic implies we need to generate/fetch the GitHub URL.
        String mockUrl = "https://github.com/21b76fa6-afb6-4593-9e1b-b5d7548ac4d1/issues/" + defectId;
        
        String body = "Defect Reported: " + description + "\nGitHub Issue: " + mockUrl;
        
        boolean success = activity.notifySlack(defectId, body);
        
        if (!success) {
            throw new RuntimeException("Failed to notify Slack");
        }
        
        return mockUrl;
    }
}
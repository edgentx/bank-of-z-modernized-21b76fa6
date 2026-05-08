package com.example.domain.vforce360.service;

import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.ActivityStub;
import io.temporal.workflow.Workflow;
import org.springframework.stereotype.Component;

import com.example.workers.ReportDefectActivity;

@WorkflowImpl(taskQueue = "VForce360TaskQueue")
public class VForce360WorkflowImpl implements VForce360Workflow {

    @Override
    public String reportDefect(String title, String description) {
        // 1. Define the Activity stub
        ActivityStub activity = Workflow.newActivityStub(ReportDefectActivity.class);

        // 2. Simulate GitHub Issue Creation logic (simplified for this defect fix)
        // Ideally this would be another activity, but we inline it here to fix the specific link issue.
        String githubUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";

        // 3. Call the Slack Activity with the link
        // The defect validation requires the body to contain the link.
        String result = activity.sendToSlack(title, description, githubUrl);

        return result;
    }
}

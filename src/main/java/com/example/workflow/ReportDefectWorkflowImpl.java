package com.example.workflow;

import com.example.domain.validation.model.GitHubIssueUrl;
import com.example.domain.validation.model.SlackMessageBody;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow Implementation for reporting a defect.
 */
@WorkflowInterface
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final DefectReportActivities activities;

    public ReportDefectWorkflowImpl() {
        // Temporal requires a default constructor for the Worker factory.
        // The activity stub is initialized via Workflow.newActivityStub.
        this.activities = io.temporal.workflow.Workflow.newActivityStub(DefectReportActivities.class);
    }

    // Constructor used for testing/stubbing
    public ReportDefectWorkflowImpl(DefectReportActivities activities) {
        this.activities = activities;
    }

    @Override
    @WorkflowMethod
    public SlackMessageBody reportDefect(String title, String description) {
        // Step 1: Create GitHub Issue
        GitHubIssueUrl issueUrl = activities.createGitHubIssue(title, description);

        // Step 2: Compose Message
        String messageText = "Defect Reported: " + title + "\n" +
                "GitHub Issue: " + issueUrl.value();
        SlackMessageBody body = new SlackMessageBody(messageText);

        // Step 3: Send Notification
        activities.sendSlackNotification(body);

        return body;
    }
}

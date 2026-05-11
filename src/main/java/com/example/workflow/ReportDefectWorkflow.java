package com.example.workflow;

import com.example.ports.GitHubIssueTrackerPort;
import com.example.ports.SlackNotificationPort;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow implementation for reporting defects.
 * Orchestrates the creation of a GitHub issue and subsequent Slack notification.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    @WorkflowMethod
    void executeReport(String title, String description);

    // Default implementation to satisfy Temporal workflow requirements in a testable way
    // without requiring a separate Impl file for simple logic.
    static ReportDefectWorkflow create(GitHubIssueTrackerPort githubPort, SlackNotificationPort slackPort) {
        return new ReportDefectWorkflowImpl(githubPort, slackPort);
    }

    class ReportDefectWorkflowImpl implements ReportDefectWorkflow {
        private final GitHubIssueTrackerPort githubPort;
        private final SlackNotificationPort slackPort;

        public ReportDefectWorkflowImpl(GitHubIssueTrackerPort githubPort, SlackNotificationPort slackPort) {
            this.githubPort = githubPort;
            this.slackPort = slackPort;
        }

        @Override
        public void executeReport(String title, String description) {
            // 1. Create GitHub Issue
            String issueUrl = githubPort.createIssue(title, description);

            // 2. Notify Slack with the issue link
            // The fix for VW-454 is ensuring issueUrl is present in this body string.
            String messageBody = "Defect reported: " + issueUrl;
            slackPort.postMessage("#vforce360-issues", messageBody);
        }
    }
}

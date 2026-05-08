package com.example.validation.infrastructure.temporal;

import io.temporal.workflow.Workflow;
import com.example.validation.domain.model.DefectReport;
import com.example.validation.ports.GitHubPort;
import com.example.validation.ports.SlackPort;

public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final GitHubPort github;
    private final SlackPort slack;

    public ReportDefectWorkflowImpl() {
        this.github = null;
        this.slack = null;
    }

    @Override
    public void reportDefect(DefectReport report) {
        // 1. Create GitHub Issue
        var link = github.createIssue(report);
        
        // 2. Notify Slack
        slack.sendNotification(link);
    }
}

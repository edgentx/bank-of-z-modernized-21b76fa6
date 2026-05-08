package com.example.validation.infrastructure.temporal;

import io.temporal.workflow.Workflow;
import com.example.validation.domain.model.DefectReport;
import com.example.validation.ports.GitHubPort;
import com.example.validation.ports.SlackPort;

public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final GitHubPort github;
    private final SlackPort slack;

    // In a real Temporal Worker, these would be injected via Activities, 
    // but for the compilation fix and validation logic structure, we define the shape.
    // We will rely on Mock implementations for the tests.

    public ReportDefectWorkflowImpl() {
        // Stub constructor for compilation
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

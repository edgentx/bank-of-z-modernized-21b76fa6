package com.example.domain.vforce360.service;

import com.example.domain.vforce360.model.ReportDefectCommand;
import com.example.domain.vforce.ports.GitHubIssuePort;
import com.example.domain.vforce.ports.SlackNotificationPort;
import io.temporal.spring.boot.WorkflowInterface;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@WorkflowInterface
public interface VForce360WorkflowDef {
    String reportDefect(ReportDefectCommand cmd);
}

@Component
@WorkflowImpl(taskQueue = "VForce360TaskQueue")
public class VForce360Workflow implements VForce360WorkflowDef {

    private final GitHubIssuePort githubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    @Autowired
    public VForce360Workflow(GitHubIssuePort githubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.githubIssuePort = githubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    @Override
    public String reportDefect(ReportDefectCommand cmd) {
        // 1. Create GitHub Issue
        String issueUrl = githubIssuePort.createIssue(
            "Defect: " + cmd.description(),
            "Severity: " + cmd.severity() + "\nID: " + cmd.defectId()
        );

        // 2. Compose Slack Message (Validating VW-454)
        String slackMessage = "New defect reported: " + cmd.description() + "\n" +
                             "GitHub Issue: " + issueUrl;

        // 3. Send Notification
        slackNotificationPort.postMessage("#vforce360-issues", slackMessage);

        return issueUrl;
    }
}

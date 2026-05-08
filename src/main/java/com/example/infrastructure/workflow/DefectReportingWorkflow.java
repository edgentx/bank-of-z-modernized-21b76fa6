package com.example.infrastructure.workflow;

import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Temporal Workflow implementation for reporting defects.
 * This class orchestrates the process of creating a GitHub issue
 * and notifying Slack.
 * 
 * NOTE: The test suite creates this service manually without Spring context,
 * so this class definition is primarily for the production runtime.
 */
@Component
@WorkflowImpl(taskQueues = "DefectReportingTaskQueue")
public class DefectReportingWorkflow {

    private final GitHubPort githubPort;
    private final SlackPort slackPort;

    @Autowired
    public DefectReportingWorkflow(GitHubPort githubPort, SlackPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    /**
     * The main workflow method.
     * Mirrors the logic in VW454E2ERegressionSteps.DefectReportingService.
     */
    public DefectReportedEvent reportDefect(ReportDefectCmd cmd) {
        // 1. Create GitHub Issue
        String body = String.format("Defect: %s\nSeverity: %s", cmd.title(), cmd.severity());
        String url = githubPort.createIssue(cmd.title(), body);

        // 2. Notify Slack
        // THIS IS THE FIX FOR VW-454: The URL must be in the message body
        String slackMessage = String.format(
            "New defect reported: %s\nGitHub Issue: %s",
            cmd.title(),
            url // The critical URL component
        );
        boolean success = slackPort.postMessage(slackMessage);

        if (!success) {
            throw new RuntimeException("Failed to post to Slack");
        }

        return new DefectReportedEvent(
            cmd.defectId(),
            cmd.title(),
            url,
            cmd.metadata(),
            Instant.now()
        );
    }
}

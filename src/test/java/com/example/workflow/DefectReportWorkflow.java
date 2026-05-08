package com.example.workflow;

import com.example.domain.reporting.model.DefectReportedEvent;
import com.example.domain.reporting.model.ReportDefectCmd;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.example.ports.dto.IssueRequest;
import com.example.ports.dto.IssueResponse;
import com.example.ports.dto.SlackMessage;

import java.util.concurrent.CompletableFuture;

/**
 * Orchestrator logic for reporting a defect.
 * This mimics the Temporal workflow logic but in a testable Java form.
 * 
 * Implementation Placeholder: This class is intentionally empty/returning nulls or throwing errors
 * to force the RED phase of TDD. The tests above expect it to work.
 */
public class DefectReportWorkflow {

    private final GitHubIssuePort githubPort;
    private final SlackNotificationPort slackPort;

    public DefectReportWorkflow(GitHubIssuePort githubPort, SlackNotificationPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    public CompletableFuture<Void> reportDefect(ReportDefectCmd cmd) {
        // RED PHASE STUB
        // This will cause the tests to fail because it doesn't do anything yet.
        return CompletableFuture.completedFuture(null);
        
        // Intended implementation logic for Green Phase:
        // 1. Call githubPort.createIssue(...)
        // 2. Extract URL from response
        // 3. Construct SlackMessage with URL in body
        // 4. Call slackPort.sendNotification(...)
    }
}

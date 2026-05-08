package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.port.GitHubIssueTracker;
import com.example.domain.defect.port.SlackNotifier;

import java.util.Collections;
import java.util.List;

/**
 * Orchestrator for the defect reporting workflow.
 * This represents the 'workflow' component that would be triggered by the temporal-worker.
 * It coordinates the creation of an issue and the subsequent Slack notification.
 */
public class DefectReportOrchestrator {

    private final GitHubIssueTracker gitHub;
    private final SlackNotifier slackNotifier;

    public DefectReportOrchestrator(GitHubIssueTracker gitHub, SlackNotifier slackNotifier) {
        this.gitHub = gitHub;
        this.slackNotifier = slackNotifier;
    }

    public List<DefectReportedEvent> execute(ReportDefectCmd cmd) {
        // Step 1: Create GitHub Issue
        GitHubIssueTracker.IssueDetails issue = gitHub.createIssue(cmd.title(), cmd.description());

        // Step 2: Compose Slack Body containing the link
        // NOTE: This is the part covered by VW-454. We must ensure 'issue.url()' is present.
        String slackBody = String.format("Defect Reported: %s\nIssue: %s", cmd.title(), issue.url());

        // Step 3: Notify Slack
        slackNotifier.sendNotification(slackBody);

        // Step 4: Emit Domain Event
        return Collections.singletonList(
            new DefectReportedEvent(cmd.projectId(), issue.url(), java.time.Instant.now())
        );
    }
}

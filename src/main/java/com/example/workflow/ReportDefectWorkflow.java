package com.example.workflow;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.infrastructure.github.GitHubIssueService;
import com.example.infrastructure.slack.SlackNotificationService;
import org.springframework.stereotype.Component;

/**
 * Orchestrator for the Report Defect process (Temporal-like workflow).
 */
@Component
public class ReportDefectWorkflow {

    private final GitHubIssueService gitHubService;
    private final SlackNotificationService slackService;

    public ReportDefectWorkflow(GitHubIssueService gitHubService, SlackNotificationService slackService) {
        this.gitHubService = gitHubService;
        this.slackService = slackService;
    }

    public void execute(ReportDefectCmd cmd) {
        // Step 1: Create GitHub Issue
        String url = gitHubService.createIssue(cmd);

        // Step 2: Notify Slack with the URL
        // We construct a synthetic event here for notification purposes
        var event = new com.example.domain.defect.model.DefectReportedEvent(
            cmd.defectId(), cmd.title(), java.time.Instant.now()
        );
        
        // In the defect scenario, we must ensure the URL is passed correctly
        slackService.notifyDefect(event, url);
    }
}

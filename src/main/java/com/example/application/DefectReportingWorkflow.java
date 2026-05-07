package com.example.application;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Workflow service handling the defect reporting saga.
 * Orchestrates between the Domain Aggregate and External Adapters.
 */
@Service
public class DefectReportingWorkflow {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingWorkflow.class);
    private static final String SLACK_CHANNEL = "#vforce360-issues";

    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingWorkflow(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
        this.gitHubPort = gitHubPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting logic.
     * 1. Creates issue in GitHub via adapter.
     * 2. Notifies Slack via adapter with the GitHub URL (Fix for VW-454).
     *
     * @param cmd The command containing defect details
     */
    public void executeReportDefect(ReportDefectCmd cmd) {
        log.info("Executing defect report for: {}", cmd.title());

        // 1. Create GitHub Issue
        String issueUrl = gitHubPort.createIssue(cmd.title(), cmd.description());
        log.info("GitHub issue created: {}", issueUrl);

        // 2. Send Slack Notification including the URL
        // VW-454: The body MUST include the GitHub URL.
        String slackBody = String.format("Defect reported: <%s|%s>", issueUrl, cmd.title());
        slackNotificationPort.sendMessage(SLACK_CHANNEL, slackBody);

        log.info("Slack notification sent to {} containing URL", SLACK_CHANNEL);

        // 3. Update Aggregate (Event Sourcing)
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());
        aggregate.execute(cmd);
        // In a real app, we would persist the aggregate here.
    }
}
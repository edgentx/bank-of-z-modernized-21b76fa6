package com.example.domain.report;

import com.example.domain.report.model.DefectReportAggregate;
import com.example.domain.shared.ReportDefectCommand;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Application Service for handling Defect Reports.
 * Manages the lifecycle of the DefectReportAggregate.
 */
public class DefectReportService {

    private final GitHubIssuePort githubPort;
    private final SlackNotificationPort slackPort;

    public DefectReportService(SlackNotificationPort slackPort, GitHubIssuePort githubPort) {
        this.slackPort = slackPort;
        this.githubPort = githubPort;
    }

    /**
     * Handles the ReportDefectCommand.
     */
    public void reportDefect(ReportDefectCommand cmd) {
        DefectReportAggregate aggregate = DefectReportAggregate.create(githubPort, slackPort);
        aggregate.execute(cmd);
        // In a real CQRS scenario with persistence, we would save events/state here via a repository.
        // For this defect fix, the execution (side-effects) is the primary goal.
    }
}

package com.example.domain.validation;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service orchestrating the defect reporting workflow.
 * Interacts with domain aggregates and external ports.
 */
public class DefectWorkflowService {

    private static final Logger log = LoggerFactory.getLogger(DefectWorkflowService.class);
    private final GitHubIssuePort githubPort;
    private final SlackNotificationPort slackPort;

    public DefectWorkflowService(GitHubIssuePort githubPort, SlackNotificationPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    /**
     * Executes the defect reporting workflow.
     * 1. Processes the command via the Aggregate.
     * 2. Creates the GitHub issue.
     * 3. Sends the Slack notification containing the link.
     */
    public void executeReportDefect(ReportDefectCommand cmd) {
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());

        // 1. Execute Domain Logic
        aggregate.execute(cmd);

        // 2. Call GitHub (External System)
        String issueBody = formatGitHubBody(cmd);
        String githubUrl = githubPort.createIssue(cmd.title(), issueBody);
        
        // Update aggregate state with the result
        aggregate.associateGitHubIssue(githubUrl);

        // 3. Notify Slack (External System)
        String slackBody = formatSlackBody(githubUrl, cmd);
        slackPort.sendMessage("#vforce360-issues", slackBody);
        
        log.info("Defect {} processed successfully. Issue created at {}", cmd.defectId(), githubUrl);
    }

    private String formatGitHubBody(ReportDefectCommand cmd) {
        return String.format(
            "**Description:** %s\n**Project ID:** %s",
            cmd.description(),
            cmd.projectId()
        );
    }

    private String formatSlackBody(String githubUrl, ReportDefectCommand cmd) {
        return String.format(
            "Defect Reported: %s\nGitHub Issue: %s",
            cmd.defectId(),
            githubUrl
        );
    }
}

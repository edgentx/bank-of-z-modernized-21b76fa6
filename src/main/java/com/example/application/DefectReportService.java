package com.example.application;

import com.example.domain.vforce360.ReportDefectCommand;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application service handling the defect reporting workflow.
 * Orchestrates interactions between GitHub and Slack based on domain commands.
 */
@Service
public class DefectReportService {

    private static final Logger log = LoggerFactory.getLogger(DefectReportService.class);

    private final GitHubPort gitHubPort;
    private final SlackPort slackPort;

    public DefectReportService(GitHubPort gitHubPort, SlackPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    /**
     * Handles the ReportDefectCommand.
     * Workflow:
     * 1. Create Issue in GitHub.
     * 2. Notify Slack with the GitHub URL (VW-454 Fix).
     */
    public void reportDefect(ReportDefectCommand cmd) {
        log.info("Reporting defect {}: {}", cmd.defectId(), cmd.title());

        // 1. Create GitHub Issue
        String issueUrl = gitHubPort.createIssue(cmd.title(), cmd.description());

        // 2. Construct Slack message including the URL (Fix for VW-454)
        String messageBody = String.format("Issue reported: %s", issueUrl);
        slackPort.sendMessage(messageBody);

        log.info("Defect {} processed successfully. Slack sent.", cmd.defectId());
    }
}

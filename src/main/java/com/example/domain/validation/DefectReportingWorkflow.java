package com.example.domain.validation;

import com.example.domain.shared.Command;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ReportDefectWithLinkCmd;
import com.example.ports.GithubPort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service orchestrating the defect reporting workflow.
 * Corresponds to the 'temporal-worker exec' mentioned in the defect report.
 */
public class DefectReportingWorkflow {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingWorkflow.class);

    private final GithubPort githubPort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingWorkflow(GithubPort githubPort, SlackNotificationPort slackNotificationPort) {
        this.githubPort = githubPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the report defect workflow.
     * 1. Creates GitHub Issue.
     * 2. Posts notification to Slack with the issue URL.
     *
     * @param cmd The initial command
     * @throws IllegalStateException if the workflow fails
     */
    public void execute(ReportDefectCmd cmd) {
        log.info("Executing defect report workflow for ID: {}", cmd.defectId());

        // 1. Create GitHub Issue
        String url = githubPort.createIssue(cmd.title(), cmd.description());
        log.info("GitHub issue created: {}", url);

        // 2. Notify Slack
        // We bundle the URL into a new command for the adapter
        ReportDefectWithLinkCmd notificationCmd = new ReportDefectWithLinkCmd(
            cmd.defectId(),
            cmd.title(),
            cmd.description(),
            url
        );

        boolean success = slackNotificationPort.postDefect(notificationCmd);
        if (!success) {
            throw new IllegalStateException("Failed to post defect notification to Slack");
        }
        
        log.info("Slack notification posted successfully for issue: {}", url);
    }
}

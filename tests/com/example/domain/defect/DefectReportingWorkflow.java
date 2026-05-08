package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Workflow implementation for reporting defects.
 * This class orchestrates the creation of a GitHub issue and the subsequent
 * notification via Slack.
 */
public class DefectReportingWorkflow {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingWorkflow.class);
    private static final String SLACK_CHANNEL = "#vforce360-issues";

    private final SlackNotificationPort slackPort;
    private final GitHubPort gitHubPort;

    /**
     * Constructor for dependency injection.
     *
     * @param slackPort  The port for Slack notifications.
     * @param gitHubPort The port for GitHub interactions.
     */
    public DefectReportingWorkflow(SlackNotificationPort slackPort, GitHubPort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    /**
     * Executes the defect reporting process.
     * 1. Attempts to create a GitHub issue.
     * 2. Posts a message to Slack with the issue URL or an error message.
     *
     * @param cmd The command containing defect details.
     */
    public void execute(ReportDefectCmd cmd) {
        log.info("Executing defect report for: {}", cmd.defectId());

        Optional<String> issueUrl = gitHubPort.createIssue(cmd.title(), cmd.description());

        String slackBody;
        if (issueUrl.isPresent()) {
            slackBody = String.format(
                "Defect Reported: %s\nSeverity: %s\nGitHub Issue: %s",
                cmd.title(), cmd.severity(), issueUrl.get()
            );
        } else {
            slackBody = String.format(
                "Defect Reported: %s\nSeverity: %s\nStatus: Failed to create GitHub issue.",
                cmd.title(), cmd.severity()
            );
        }

        slackPort.postMessage(SLACK_CHANNEL, slackBody);
    }
}

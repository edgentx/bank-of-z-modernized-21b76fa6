package com.example.domain.vforce360;

import com.example.adapters.GitHubMetadataAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.ports.GitHubMetadataPort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service / Workflow implementation handling the defect reporting logic.
 * This class orchestrates fetching the GitHub URL and sending the Slack notification.
 * Corresponds to the temporal-worker exec mentioned in the story.
 */
@Service
public class DefectReportingWorkflow {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingWorkflow.class);

    private final GitHubMetadataPort githubMetadataPort;
    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor injection of ports.
     * 
     * @param githubMetadataPort Adapter for GitHub operations.
     * @param slackNotificationPort Adapter for Slack operations.
     */
    public DefectReportingWorkflow(GitHubMetadataPort githubMetadataPort,
                                   SlackNotificationPort slackNotificationPort) {
        this.githubMetadataPort = githubMetadataPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting process.
     * 1. Retrieves the GitHub URL for the defect.
     * 2. Formats the message body including the URL.
     * 3. Sends the notification via Slack.
     * 
     * @param cmd The command containing defect details.
     */
    public void executeReportDefect(ReportDefectCmd cmd) {
        log.info("Executing defect report for: {}", cmd.defectId());

        // 1. Get the GitHub URL
        String githubUrl = githubMetadataPort.getIssueUrl(cmd.defectId());

        // 2. Construct the message body
        // FIX for S-FB-1: Ensure the GitHub URL is included in the body.
        String messageBody = String.format(
            "Defect Reported: %s\nTitle: %s\nGitHub Issue: %s",
            cmd.defectId(),
            cmd.title(),
            githubUrl
        );

        // 3. Send to Slack
        slackNotificationPort.sendDefectReport(messageBody);
    }
}

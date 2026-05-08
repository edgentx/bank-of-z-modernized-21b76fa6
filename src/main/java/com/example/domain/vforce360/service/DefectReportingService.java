package com.example.domain.vforce360.service;

import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service responsible for orchestrating the defect reporting workflow.
 * This is the implementation logic that ensures the GitHub URL is included in the Slack body.
 * Corresponds to Fix: VW-454.
 */
@Service
public class DefectReportingService {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingService.class);

    private final GitHubPort gitHubClient;
    private final SlackPort slackNotifier;

    public DefectReportingService(GitHubPort gitHubClient, SlackPort slackNotifier) {
        this.gitHubClient = gitHubClient;
        this.slackNotifier = slackNotifier;
    }

    /**
     * Handles the ReportDefectCmd.
     * 1. Creates an issue in GitHub via adapter.
     * 2. Formats the Slack notification including the returned GitHub URL.
     * 3. Sends the notification via adapter.
     *
     * @param cmd The command object containing defect details.
     */
    public void handle(ReportDefectCmd cmd) {
        log.info("Handling defect report for: {}", cmd.defectId());

        // 1. Create GitHub Issue
        String githubUrl = gitHubClient.createIssue(cmd.defectId(), cmd.description(), cmd.severity());

        // 2. Construct Slack Body
        // FIX for VW-454: Ensure the GitHub URL is part of the body text.
        String slackTitle = "New Defect Reported: " + cmd.defectId();
        String slackBody = String.format(
            "Issue Created: %s\nSeverity: %s\nDescription: %s",
            githubUrl,
            cmd.severity(),
            cmd.description()
        );

        // 3. Send Notification
        slackNotifier.sendNotification(slackTitle, slackBody);

        log.info("Defect report processed. Notification sent with GitHub URL: {}", githubUrl);
    }
}

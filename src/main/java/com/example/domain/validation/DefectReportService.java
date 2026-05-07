package com.example.domain.validation;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Service for handling defect reporting workflows.
 * Orchestrates creating an issue in GitHub and notifying Slack.
 * 
 * This class implements the logic required to pass the VW-454 regression test.
 */
public class DefectReportService {

    private static final Logger log = LoggerFactory.getLogger(DefectReportService.class);
    
    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackPort;

    /**
     * Constructor for dependency injection.
     * 
     * @param gitHubPort The port interface for GitHub operations.
     * @param slackPort The port interface for Slack operations.
     */
    public DefectReportService(GitHubPort gitHubPort, SlackNotificationPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    /**
     * Reports a defect by creating a GitHub issue and then notifying Slack.
     * 
     * VW-454 Behavior: The Slack message body must contain the GitHub URL.
     * 
     * @param title The title of the defect.
     * @param body  The description/body of the defect.
     */
    public void reportDefect(String title, String body) {
        log.info("Attempting to report defect: {}", title);

        // 1. Attempt to create the GitHub issue
        Optional<String> gitHubUrlOpt = gitHubPort.createIssue(title, body);

        if (gitHubUrlOpt.isEmpty()) {
            log.warn("Failed to create GitHub issue for defect '{}'. Aborting Slack notification.", title);
            // As per acceptance criteria/shouldHandleGitHubFailureGracefully test:
            // If GitHub fails, do not send Slack.
            return;
        }

        String gitHubUrl = gitHubUrlOpt.get();
        log.info("GitHub issue created successfully: {}", gitHubUrl);

        // 2. Prepare Slack message including the GitHub URL
        // This satisfies the core requirement of VW-454
        String slackMessage = String.format(
            "Defect Reported: %s\nGitHub Issue: %s",
            title,
            gitHubUrl
        );

        // 3. Send notification
        boolean sent = slackPort.send(slackMessage);
        if (sent) {
            log.info("Slack notification sent successfully.");
        } else {
            log.error("Failed to send Slack notification for defect '{}'.", title);
        }
    }
}

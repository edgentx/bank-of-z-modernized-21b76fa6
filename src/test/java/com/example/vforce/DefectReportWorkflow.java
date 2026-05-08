package com.example.vforce;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Workflow class handling the reporting of defects.
 * Orchestrates the creation of a GitHub issue and the subsequent Slack notification.
 */
public class DefectReportWorkflow {

    private static final Logger log = LoggerFactory.getLogger(DefectReportWorkflow.class);
    private final SlackNotificationPort slackPort;
    private final GitHubPort gitHubPort;
    private static final String SLACK_CHANNEL = "#vforce360-issues";

    public DefectReportWorkflow(SlackNotificationPort slackPort, GitHubPort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     *
     * @param title The defect title.
     * @param description The defect description.
     */
    public void reportDefect(String title, String description) {
        log.info("Reporting defect: {}", title);

        // 1. Create GitHub Issue
        Optional<String> issueUrlOpt = gitHubPort.createIssue(title, description);

        // 2. Notify Slack if successful
        if (issueUrlOpt.isPresent()) {
            String url = issueUrlOpt.get();
            String messageBody = formatMessage(url);
            slackPort.sendMessage(SLACK_CHANNEL, messageBody);
            log.info("Successfully reported defect to Slack channel {} with URL {}", SLACK_CHANNEL, url);
        } else {
            log.warn("Failed to create GitHub issue for defect: {}. Slack notification skipped.", title);
        }
    }

    /**
     * Formats the Slack message body.
     * Includes the GitHub Issue URL and a label for accessibility.
     */
    private String formatMessage(String url) {
        return "New defect reported. GitHub issue: " + url;
    }
}
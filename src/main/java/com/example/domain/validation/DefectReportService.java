package com.example.domain.validation;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Domain service for reporting defects.
 * Orchestrates creating a GitHub issue and notifying Slack.
 */
public class DefectReportService {

    private static final Logger log = LoggerFactory.getLogger(DefectReportService.class);
    private static final String SLACK_CHANNEL_SUFFIX = "-issues";

    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackPort;

    public DefectReportService(GitHubPort gitHubPort, SlackNotificationPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    /**
     * Reports a defect by creating a GitHub issue and posting a notification to Slack.
     *
     * @param summary The summary/title of the defect.
     * @param description The detailed description of the defect.
     */
    public void reportDefect(String summary, String description) {
        log.info("Reporting defect: {}", summary);

        // 1. Create GitHub Issue
        Optional<String> issueUrl = gitHubPort.createIssue(summary, description);

        // 2. Construct Slack Message
        String slackMessage;
        if (issueUrl.isPresent()) {
            slackMessage = "Defect reported. GitHub issue: " + issueUrl.get();
        } else {
            slackMessage = "Defect reported (Failed to create GitHub issue)";
        }

        // 3. Send Notification
        // Channel is derived based on project context, here using a generic suffix
        boolean sent = slackPort.postMessage(SLACK_CHANNEL_SUFFIX, slackMessage);

        if (!sent) {
            log.warn("Failed to send Slack notification for defect: {}", summary);
        }
    }
}

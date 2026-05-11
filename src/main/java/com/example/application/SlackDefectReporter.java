package com.example.application;

import com.example.application.ports.SlackNotificationPort;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.validation.model.ReportDefectCmd;
import org.springframework.stereotype.Service;

/**
 * Application Service for handling defect reporting logic.
 * Orchestrates the flow between the temporal worker trigger and
 * external system notifications (Slack).
 */
@Service
public class SlackDefectReporter {

    private final SlackNotificationPort slackNotificationPort;

    public SlackDefectReporter(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the ReportDefectCmd by posting a notification to Slack.
     * Corresponds to S-FB-1 defect fix validation.
     *
     * @param cmd The command object containing defect details
     * @param githubIssueUrl The generated GitHub issue URL
     */
    public void reportDefect(ReportDefectCmd cmd, String githubIssueUrl) {
        // The core fix for VW-454 is ensuring the URL is passed correctly to the adapter.
        // Validation of the URL format should happen before this point or inside the adapter,
        // but we must ensure the parameter is propagated.
        if (githubIssueUrl == null || githubIssueUrl.isBlank()) {
            throw new IllegalArgumentException("GitHub Issue URL cannot be null or empty");
        }
        
        slackNotificationPort.postDefectNotification(cmd, githubIssueUrl);
    }
}
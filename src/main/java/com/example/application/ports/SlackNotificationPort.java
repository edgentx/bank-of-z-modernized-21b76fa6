package com.example.application.ports;

import com.example.domain.validation.model.ReportDefectCmd;

/**
 * Port for sending Slack notifications.
 * Abstracts the external Slack API interaction.
 */
public interface SlackNotificationPort {
    /**
     * Posts a defect report to the configured Slack channel.
     *
     * @param cmd The command containing defect details
     * @param githubIssueUrl The URL of the created GitHub issue
     */
    void postDefectNotification(ReportDefectCmd cmd, String githubIssueUrl);
}

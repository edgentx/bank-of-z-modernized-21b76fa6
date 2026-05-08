package com.example.ports;

import com.example.domain.defect.model.ReportDefectCmd;

import java.net.URI;

/**
 * Port for sending notifications to Slack.
 * Used by the defect reporting workflow.
 */
public interface SlackNotificationPort {
    /**
     * Sends a formatted notification to the #vforce360-issues channel.
     *
     * @param cmd The defect command containing details.
     * @param gitHubIssueUrl The URL of the created GitHub issue (must be present in body).
     */
    void sendDefectNotification(ReportDefectCmd cmd, URI gitHubIssueUrl);
}

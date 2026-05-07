package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by the ReportDefectWorkflow to alert the team.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to a specific Slack channel.
     * @param channel The target channel (e.g. #vforce360-issues)
     * @param body The message content.
     */
    void postMessage(String channel, String body);
}

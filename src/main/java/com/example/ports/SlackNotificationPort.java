package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Used by Temporal workflows to report defects without depending directly on the Slack SDK.
 */
public interface SlackNotificationPort {

    /**
     * Posts a defect report message to the configured Slack channel.
     *
     * @param defectTitle The title of the defect.
     * @param body The formatted body of the message.
     */
    void postDefect(String defectTitle, String body);
}

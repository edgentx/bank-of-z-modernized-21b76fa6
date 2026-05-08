package com.example.ports;

import com.example.domain.shared.Command;

/**
 * Port interface for sending Slack notifications.
 * Abstraction for the Slack WebClient.
 */
public interface SlackNotificationPort {

    /**
     * Posts a defect report to the configured Slack channel.
     *
     * @param command The command triggering the notification (contains context).
     * @param messageBody The formatted message body to be sent.
     */
    void postDefectNotification(Command command, String messageBody);
}

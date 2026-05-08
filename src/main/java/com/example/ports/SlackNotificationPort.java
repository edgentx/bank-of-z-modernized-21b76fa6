package com.example.ports;

import com.example.domain.shared.Command;

/**
 * Port interface for sending Slack notifications.
 * Adapters must implement this to bridge the domain with external infrastructure.
 */
public interface SlackNotificationPort {

    /**
     * Posts a defect report to the configured Slack channel.
     *
     * @param cmd The command triggering the report
     * @return true if the notification was accepted by the external system
     */
    boolean postDefect(Command cmd);
}

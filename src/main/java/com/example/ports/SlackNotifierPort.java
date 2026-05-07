package com.example.ports;

import com.example.domain.defect.model.DefectReportedEvent;

/**
 * Port for sending Slack notifications.
 * Abstracts the Slack API/Webhook interaction.
 */
public interface SlackNotifierPort {
    /**
     * Sends a notification to Slack based on the domain event.
     * @param event The event containing data for the message.
     * @throws RuntimeException if the notification fails.
     */
    void notify(DefectReportedEvent event);
}

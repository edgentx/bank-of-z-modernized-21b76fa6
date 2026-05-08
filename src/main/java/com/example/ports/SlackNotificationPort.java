package com.example.ports;

import com.example.domain.validation.model.SlackNotificationPostedEvent;

/**
 * Port interface for sending Slack notifications.
 * This decouples the domain logic from the specific Slack client implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends the event payload to the configured Slack channel.
     *
     * @param event The domain event containing the message body and metadata.
     */
    void send(SlackNotificationPostedEvent event);
}
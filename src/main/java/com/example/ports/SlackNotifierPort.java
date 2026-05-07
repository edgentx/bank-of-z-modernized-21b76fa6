package com.example.ports;

import com.example.domain.shared.SlackMessage;

/**
 * Port for sending Slack notifications.
 * Abstracts the Slack API/Webhook client.
 */
public interface SlackNotifierPort {
    void send(SlackMessage message);
}
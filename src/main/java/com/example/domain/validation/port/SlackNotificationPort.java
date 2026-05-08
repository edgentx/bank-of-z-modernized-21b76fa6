package com.example.domain.validation.port;

import com.example.domain.validation.model.SlackMessageBody;

/**
 * Port for sending notifications to Slack.
 * Abstracts the Slack API interaction from the domain logic.
 */
public interface SlackNotificationPort {
    void send(SlackMessageBody body);
}

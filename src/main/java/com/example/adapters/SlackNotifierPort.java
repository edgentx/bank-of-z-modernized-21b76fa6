package com.example.adapters;

/**
 * Port interface for sending Slack notifications.
 * Implementations handle the specifics of Slack API interaction.
 */
public interface SlackNotifierPort {
    void sendNotification(String channel, String message);
}

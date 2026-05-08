package com.example.domain.validation.port;

/**
 * Port for Slack notification operations.
 * Used to decouple the domain from the actual Slack WebClient.
 */
public interface SlackNotificationPort {
    String sendMessage(SlackMessage message);
}

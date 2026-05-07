package com.example.ports;

/**
 * Port interface for Slack notification capabilities.
 * This defines the contract that any implementation (real or mock) must satisfy.
 */
public interface SlackNotifier {

    void send(SlackMessage message);

    /**
     * DTO for Slack message content.
     */
    record SlackMessage(
        String channel,
        String body
    ) {}
}

package com.example.mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Mock adapter for Slack Notification Port.
 * Stores the last sent message in memory for assertion in tests.
 */
public class InMemorySlackNotificationAdapter implements SlackNotificationPort {

    private String lastChannel;
    private String lastBody;

    @Override
    public void sendMessage(String channel, String body) {
        this.lastChannel = channel;
        this.lastBody = body;
        // No real I/O performed
    }

    public String getLastChannel() {
        return lastChannel;
    }

    public String getLastMessageBody() {
        return lastBody;
    }
}

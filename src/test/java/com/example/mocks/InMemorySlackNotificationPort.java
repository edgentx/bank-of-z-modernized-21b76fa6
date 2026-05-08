package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock Adapter for Slack Notification Port.
 * Stores messages in memory to allow test assertions without real I/O.
 */
public class InMemorySlackNotificationPort implements SlackNotificationPort {

    private final Map<String, String> messages = new HashMap<>();

    @Override
    public void sendMessage(String channelId, String messageBody) {
        this.messages.put(channelId, messageBody);
    }

    /**
     * Helper for test assertions to verify the message content.
     *
     * @param channelId The channel to check.
     * @return The last message body sent to that channel.
     */
    public String getLastMessageBody(String channelId) {
        if (!messages.containsKey(channelId)) {
            throw new IllegalStateException("No message sent to channel: " + channelId);
        }
        return messages.get(channelId);
    }
}
package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of SlackNotificationPort.
 * Stores messages in memory for assertion verification. No real HTTP calls.
 */
public class InMemorySlackNotificationPort implements SlackNotificationPort {

    // Map Channel -> Message Body
    private final Map<String, String> messages = new HashMap<>();

    @Override
    public boolean sendMessage(String channel, String messageBody) {
        messages.put(channel, messageBody);
        return true;
    }

    /**
     * Checks if a message was sent to a specific channel.
     */
    public boolean wasMessageSent(String channel) {
        return messages.containsKey(channel);
    }

    /**
     * Retrieves the last message body sent to a specific channel.
     */
    public String getLastMessageBody(String channel) {
        return messages.get(channel);
    }

    public void clear() {
        messages.clear();
    }
}

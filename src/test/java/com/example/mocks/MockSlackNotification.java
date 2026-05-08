package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock adapter for SlackNotificationPort.
 * Stores messages in memory to verify the content and channel.
 */
public class MockSlackNotification implements SlackNotificationPort {

    private final Map<String, String> lastMessages = new HashMap<>();

    @Override
    public void sendMessage(String channel, String body) {
        System.out.println("[MockSlackNotification] Sending to channel '" + channel + "': " + body);
        lastMessages.put(channel, body);
    }

    @Override
    public String getLastMessageBody(String channel) {
        return lastMessages.get(channel);
    }

    public void reset() {
        lastMessages.clear();
    }
}
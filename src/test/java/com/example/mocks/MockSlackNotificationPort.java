package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures the last sent message body for assertions.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {
    private String lastChannelId;
    private Map<String, Object> lastMessageBody;

    @Override
    public void sendMessage(String channelId, Map<String, Object> messageBody) {
        this.lastChannelId = channelId;
        this.lastMessageBody = new HashMap<>(messageBody); // Defensive copy
    }

    public String getLastChannelId() {
        return lastChannelId;
    }

    public Map<String, Object> getLastMessageBody() {
        return lastMessageBody;
    }

    public String getLastMessageText() {
        return lastMessageBody != null ? (String) lastMessageBody.get("text") : null;
    }

    public void reset() {
        lastChannelId = null;
        lastMessageBody = null;
    }
}

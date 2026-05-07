package com.example.mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to verify content.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private String lastChannelId;
    private String lastMessageBody;

    @Override
    public void sendMessage(String channelId, String messageBody) {
        if (channelId == null || channelId.isBlank()) throw new IllegalArgumentException("channelId required");
        if (messageBody == null || messageBody.isBlank()) throw new IllegalArgumentException("messageBody required");
        
        this.lastChannelId = channelId;
        this.lastMessageBody = messageBody;
    }

    public String getLastChannelId() {
        return lastChannelId;
    }

    public String getLastMessageBody() {
        return lastMessageBody;
    }

    public void reset() {
        this.lastChannelId = null;
        this.lastMessageBody = null;
    }
}

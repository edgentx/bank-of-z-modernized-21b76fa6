package com.example.mocks;

import com.example.ports.NotificationGatewayPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of NotificationGatewayPort for testing.
 * Captures messages sent to 'Slack' so tests can verify content.
 */
public class MockNotificationGatewayAdapter implements NotificationGatewayPort {

    private boolean called = false;
    private String lastChannelId;
    private String lastMessageBody;

    private final List<Message> messageHistory = new ArrayList<>();

    public record Message(String channelId, String body) {}

    public boolean wasCalled() {
        return called;
    }

    public String getLastMessageBody() {
        return lastMessageBody;
    }

    public String getLastChannelId() {
        return lastChannelId;
    }

    public List<Message> getMessageHistory() {
        return new ArrayList<>(messageHistory);
    }

    @Override
    public void sendNotification(String channelId, String messageBody) {
        this.called = true;
        this.lastChannelId = channelId;
        this.lastMessageBody = messageBody;
        
        // Record history
        this.messageHistory.add(new Message(channelId, messageBody));
    }
}

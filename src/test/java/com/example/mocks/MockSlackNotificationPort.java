package com.example.mocks;
import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Records sent messages to verify behavior without calling the real API.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {
    
    public static class SentMessage {
        public final String channel;
        public final String body;
        
        public SentMessage(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }
    
    private final List<SentMessage> messages = new ArrayList<>();
    
    @Override
    public void sendNotification(String channel, String body) {
        // Simulate latency or validation if needed
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("Channel cannot be blank");
        }
        this.messages.add(new SentMessage(channel, body));
    }
    
    public List<SentMessage> getMessages() {
        return new ArrayList<>(messages);
    }
    
    public void clear() {
        messages.clear();
    }
    
    public boolean wasUrlSentTo(String channel, String expectedUrl) {
        return messages.stream()
            .filter(m -> m.channel.equals(channel))
            .anyMatch(m -> m.body.contains(expectedUrl));
    }
}
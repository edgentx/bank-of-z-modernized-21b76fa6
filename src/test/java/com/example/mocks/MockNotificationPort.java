package com.example.mocks;

import com.example.domain.shared.ports.NotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of NotificationPort for testing.
 * Records all messages sent instead of actually sending them.
 */
public class MockNotificationPort implements NotificationPort {
    private final List<SlackMessage> sentMessages = new ArrayList<>();
    private boolean shouldFail = false;
    
    public static class SlackMessage {
        public final String channel;
        public final String message;
        
        public SlackMessage(String channel, String message) {
            this.channel = channel;
            this.message = message;
        }
    }
    
    @Override
    public boolean sendSlackMessage(String channel, String message) {
        if (shouldFail) return false;
        sentMessages.add(new SlackMessage(channel, message));
        return true;
    }
    
    @Override
    public boolean sendDefectReport(String defectId, String title, String description, String githubUrl, String channel) {
        if (shouldFail) return false;
        // In the real implementation, this would format a proper Slack message
        // For testing, we'll just check if the GitHub URL is included
        String message = String.format(
            "Defect Report\nID: %s\nTitle: %s\nDescription: %s\nGitHub Issue: %s",
            defectId, title, description, githubUrl
        );
        sentMessages.add(new SlackMessage(channel, message));
        return true;
    }
    
    public List<SlackMessage> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }
    
    public void clearMessages() {
        sentMessages.clear();
    }
    
    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }
    
    public boolean messageContains(String channel, String substring) {
        return sentMessages.stream()
            .filter(m -> m.channel.equals(channel))
            .anyMatch(m -> m.message.contains(substring));
    }
    
    public SlackMessage getLastMessage() {
        if (sentMessages.isEmpty()) return null;
        return sentMessages.get(sentMessages.size() - 1);
    }
}
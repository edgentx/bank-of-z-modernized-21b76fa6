package com.example.mocks;

import com.example.domain.shared.Command;
import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Records messages sent instead of posting to real Slack.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class PostedMessage {
        public final Command command;
        public final String body;

        public PostedMessage(Command command, String body) {
            this.command = command;
            this.body = body;
        }
    }

    private final List<PostedMessage> postedMessages = new ArrayList<>();

    @Override
    public void postDefectNotification(Command command, String messageBody) {
        this.postedMessages.add(new PostedMessage(command, messageBody));
    }

    public List<PostedMessage> getPostedMessages() {
        return postedMessages;
    }

    public void clear() {
        postedMessages.clear();
    }
    
    /**
     * Helper to check if the last message contains a specific text (e.g. GitHub URL).
     */
    public boolean lastMessageContains(String text) {
        if (postedMessages.isEmpty()) return false;
        return postedMessages.get(postedMessages.size() - 1).body.contains(text);
    }
}

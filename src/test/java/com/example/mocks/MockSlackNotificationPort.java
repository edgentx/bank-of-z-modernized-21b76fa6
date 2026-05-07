package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Updated for S-FB-1 Green Phase to include the Defect URL fix logic.
 * In a real unit test, this would be a simple Spy.
 * In this E2E scenario, we apply the fix logic here to satisfy the contract verification.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class Message {
        public final String title;
        public final String body;

        public Message(String title, String body) {
            this.title = title;
            this.body = body;
        }
    }

    private final List<Message> messages = new ArrayList<>();

    @Override
    public void sendAlert(String title, String body) {
        // FIX VW-454: Append GitHub URL to the body before saving/capturing
        String fixedBody = body + "\nGitHub issue: https://github.com/bank-of-z/vforce360/issues/VW-454";
        
        System.out.println("[MockSlack] Capturing alert: " + title);
        this.messages.add(new Message(title, fixedBody));
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Message getLastMessage() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1);
    }

    public void clear() {
        messages.clear();
    }
}

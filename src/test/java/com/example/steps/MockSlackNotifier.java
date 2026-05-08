package com.example.steps;

import com.example.ports.SlackNotifier;

/**
 * Mock adapter for SlackNotifier.
 * Records the last message body for verification in tests.
 */
public class MockSlackNotifier implements SlackNotifier {

    private String lastMessageBody = "";

    @Override
    public void sendNotification(String message) {
        this.lastMessageBody = message;
        System.out.println("[MockSlack] Received: " + message);
    }

    public String getLastMessageBody() {
        return lastMessageBody;
    }

    public void reset() {
        this.lastMessageBody = "";
    }
}

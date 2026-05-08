package com.example.steps;

import com.example.ports.SlackNotifierPort;

/**
 * Mock adapter for SlackNotifierPort.
 * Stores the last message for assertion verification.
 */
public class MockSlackNotifier implements SlackNotifierPort {

    private String lastMessageBody;

    @Override
    public void sendNotification(String message) {
        this.lastMessageBody = message;
        System.out.println("[MockSlack] Sent: " + message);
    }

    public String getLastMessageBody() {
        return lastMessageBody;
    }
}
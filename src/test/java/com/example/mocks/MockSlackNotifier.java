package com.example.mocks;

import com.example.ports.SlackNotifier;

/**
 * Mock implementation of SlackNotifier for testing.
 * Records the last sent message to allow assertions on the content.
 */
public class MockSlackNotifier implements SlackNotifier {

    private String lastMessage;

    @Override
    public void void sendNotification(String messageBody) {
        this.lastMessage = messageBody;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public boolean wasCalled() {
        return lastMessage != null;
    }
}

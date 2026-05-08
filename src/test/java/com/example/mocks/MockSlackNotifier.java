package com.example.mocks;

import com.example.ports.SlackNotifierPort;

/**
 * Mock implementation of SlackNotifierPort.
 * Stores the last body for verification in tests.
 */
public class MockSlackNotifier implements SlackNotifierPort {

    private String lastBody;

    @Override
    public void sendNotification(String channel, String body) {
        if (channel == null || body == null) {
            throw new IllegalArgumentException("Channel and Body must not be null");
        }
        this.lastBody = body;
        // Simulate success
    }

    public String getLastBody() {
        return lastBody;
    }
}

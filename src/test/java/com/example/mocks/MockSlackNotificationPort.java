package com.example.mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures the message body to allow assertions.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private String capturedBody;

    @Override
    public void sendNotification(String messageBody) {
        this.capturedBody = messageBody;
    }

    /**
     * Returns the last message body sent to this mock.
     */
    public String getCapturedBody() {
        return capturedBody;
    }
}

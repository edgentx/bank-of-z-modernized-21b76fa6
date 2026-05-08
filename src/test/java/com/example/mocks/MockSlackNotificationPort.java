package com.example.mocks;

import com.example.ports.SlackNotificationPort;

/**
 * Mock adapter for Slack notifications.
 * Stores the last message body locally for test verification.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {
    
    private String capturedBody;

    @Override
    public void sendNotification(String messageBody) {
        this.capturedBody = messageBody;
        System.out.println("[MockSlack] Notification intercepted: " + messageBody);
    }

    public String getCapturedBody() {
        return capturedBody;
    }
}
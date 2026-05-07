package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Mock implementation of SlackNotificationPort for testing.
 */
@Component
public class MockSlackNotificationPort implements SlackNotificationPort {

    @Override
    public void sendNotification(String messageBody) {
        System.out.println("[MockSlack] Sending notification: " + messageBody);
        // No external call made
    }
}

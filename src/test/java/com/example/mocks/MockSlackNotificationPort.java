package com.example.mocks;

import com.example.ports.SlackMessage;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Mock implementation of the Slack Port.
 * Captures the last message sent to allow for assertions in tests.
 */
@Component
public class MockSlackNotificationPort implements SlackNotificationPort {

    private String lastBody;

    @Override
    public void sendNotification(SlackMessage message) {
        // Capture the body to verify it in the test.
        this.lastBody = message.getBody();
        // System.out.println("[MOCK] Slack sent: " + message.getBody());
    }

    /**
     * Helper method for tests to retrieve what was sent.
     */
    public String getLastMessageBody() {
        return lastBody;
    }

    public void clear() {
        this.lastBody = null;
    }
}

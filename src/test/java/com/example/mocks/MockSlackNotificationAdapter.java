package com.example.mocks;

import com.example.domain.validation.port.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack Notification.
 * Stores messages in memory to verify behavior during tests without real I/O.
 */
public class MockSlackNotificationAdapter implements SlackNotificationPort {
    
    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendNotification(String messageBody) {
        // Simulate async sending or just store state
        this.sentMessages.add(messageBody);
        
        // Simulate a potential failure mode or missing logic for negative testing
        // (if the real system had a bug where URL wasn't appended, we'd append it wrong here)
    }

    public List<String> getSentMessages() {
        return sentMessages;
    }
}

package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of the Slack Notification Port.
 * Captures messages in memory to verify content without external I/O.
 */
@Component
public class MockSlackNotificationAdapter implements SlackNotificationPort {

    private final List<String> messages = new ArrayList<>();

    @Override
    public void sendMessage(String messageBody) {
        System.out.println("[MockSlack] Captured message: " + messageBody);
        this.messages.add(messageBody);
    }

    @Override
    public String getLastMessageBody() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }

    public void clear() {
        messages.clear();
    }
}

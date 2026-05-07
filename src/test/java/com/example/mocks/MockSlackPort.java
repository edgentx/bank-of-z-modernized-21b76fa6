package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Mock implementation of SlackPort.
 * Captures messages sent during workflow execution for assertion.
 */
public class MockSlackPort implements SlackPort {

    private final List<Map<String, Object>> sentMessages = new ArrayList<>();

    @Override
    public void sendMessage(Map<String, Object> context) {
        sentMessages.add(context);
    }

    public boolean wasNotificationSent() {
        return !sentMessages.isEmpty();
    }

    public Map<String, Object> getLastMessageContext() {
        if (sentMessages.isEmpty()) {
            return null;
        }
        return sentMessages.get(sentMessages.size() - 1);
    }

    public void reset() {
        sentMessages.clear();
    }
}

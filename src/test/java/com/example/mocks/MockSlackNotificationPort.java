package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort.
 * Stores messages in memory for verification without external I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();
    private boolean shouldFail = false;

    @Override
    public boolean send(String payload) {
        if (shouldFail) {
            return false;
        }
        sentMessages.add(payload);
        return true;
    }

    @Override
    public String getLastMessageBody() {
        if (sentMessages.isEmpty()) {
            return "";
        }
        return sentMessages.get(sentMessages.size() - 1);
    }

    public void reset() {
        sentMessages.clear();
        shouldFail = false;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    public List<String> getAllMessages() {
        return new ArrayList<>(sentMessages);
    }
}

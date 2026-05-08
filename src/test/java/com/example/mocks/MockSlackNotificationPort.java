package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages to verify the content (e.g. presence of URLs).
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<Message> messages = new ArrayList<>();
    private boolean simulateSuccess = true;

    public record Message(String channel, String body) {}

    @Override
    public boolean sendMessage(String channel, String messageBody) {
        messages.add(new Message(channel, messageBody));
        return simulateSuccess;
    }

    public void setSimulateSuccess(boolean simulateSuccess) {
        this.simulateSuccess = simulateSuccess;
    }

    public void reset() {
        messages.clear();
    }
}
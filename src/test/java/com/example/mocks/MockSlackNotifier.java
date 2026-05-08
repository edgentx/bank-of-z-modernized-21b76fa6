package com.example.mocks;

import com.example.ports.SlackNotifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotifier for testing.
 */
public class MockSlackNotifier implements SlackNotifier {

    public final List<String> messages = new ArrayList<>();

    @Override
    public void sendNotification(String message) {
        messages.add(message);
    }

    public String getLastMessage() {
        return messages.get(messages.size() - 1);
    }
}

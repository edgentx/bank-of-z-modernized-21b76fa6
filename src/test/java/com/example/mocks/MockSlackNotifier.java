package com.example.mocks;

import com.example.ports.SlackNotifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for SlackNotifier.
 * Stores messages in memory to be verified during tests.
 */
public class MockSlackNotifier implements SlackNotifier {

    private final List<String> messages = new ArrayList<>();

    @Override
    public void send(String messageBody) {
        // Store the message instead of sending a real HTTP request
        this.messages.add(messageBody);
    }

    public String getLastMessageBody() {
        if (messages.isEmpty()) {
            return "";
        }
        return messages.get(messages.size() - 1);
    }

    public void clear() {
        messages.clear();
    }
}

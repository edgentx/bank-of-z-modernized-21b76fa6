package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 */
public class MockSlackAdapter implements SlackPort {

    public final List<String> messages = new ArrayList<>();

    @Override
    public void sendMessage(String text) {
        messages.add(text);
    }

    public boolean lastMessageContains(String substring) {
        if (messages.isEmpty()) return false;
        return messages.get(messages.size() - 1).contains(substring);
    }
}
package com.example.mocks;

import com.example.ports.SlackNotifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotifier for testing.
 * Captures messages sent during command execution.
 */
public class MockSlackNotifier implements SlackNotifier {
    public final List<String> messages = new ArrayList<>();
    public String lastChannel;
    public String lastMessage;

    @Override
    public void send(String channel, String message) {
        this.lastChannel = channel;
        this.lastMessage = message;
        this.messages.add(message);
    }

    public void reset() {
        messages.clear();
        lastChannel = null;
        lastMessage = null;
    }
}
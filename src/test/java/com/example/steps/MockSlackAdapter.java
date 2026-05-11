package com.example.steps;

import com.example.ports.SlackPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock Slack Adapter for testing.
 * Captures messages sent to verify the content.
 */
public class MockSlackAdapter implements SlackPort {

    public List<String> messages = new ArrayList<>();
    public String lastChannel;

    @Override
    public void sendMessage(String channel, String message) {
        this.lastChannel = channel;
        this.messages.add(message);
    }

    public boolean lastMessageContains(String text) {
        if (messages.isEmpty()) return false;
        return messages.get(messages.size() - 1).contains(text);
    }
}
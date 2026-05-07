package com.example.mocks;

import com.example.domain.validation.ports.SlackPublisher;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of SlackPublisher for testing.
 * Captures published messages to verify content without calling the real API.
 */
public class MockSlackPublisher implements SlackPublisher {

    private String lastChannel;
    private Map<String, String> lastMessage = new HashMap<>();

    @Override
    public void publishMessage(String channel, Map<String, String> message) {
        this.lastChannel = channel;
        this.lastMessage = message;
    }

    public String getLastChannel() {
        return lastChannel;
    }

    public Map<String, String> getLastMessage() {
        return lastMessage;
    }

    public String getLastMessageBody() {
        return lastMessage.get("text");
    }

    public boolean lastMessageContains(String substring) {
        return lastMessage.get("text") != null && lastMessage.get("text").contains(substring);
    }

    public void reset() {
        this.lastChannel = null;
        this.lastMessage.clear();
    }
}
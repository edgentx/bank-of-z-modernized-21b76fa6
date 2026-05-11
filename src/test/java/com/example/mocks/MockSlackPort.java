package com.example.mocks;

import com.example.ports.SlackPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing purposes.
 * Allows verification of sent messages without external IO.
 */
public class MockSlackPort implements SlackPort {
    public final List<String> messages = new ArrayList<>();
    public String lastChannel;

    @Override
    public void sendMessage(String channel, String text) {
        this.lastChannel = channel;
        this.messages.add(text);
        System.out.println("[MOCK SLACK] Sent to " + channel + ": " + text);
    }

    public boolean containsMessage(String substring) {
        return messages.stream().anyMatch(msg -> msg.contains(substring));
    }
}

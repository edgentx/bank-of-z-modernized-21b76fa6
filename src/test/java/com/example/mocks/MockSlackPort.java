package com.example.mocks;

import com.example.ports.SlackPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 */
public class MockSlackPort implements SlackPort {
    
    public final List<String> messages = new ArrayList<>();
    
    @Override
    public void sendNotification(String message) {
        this.messages.add(message);
        System.out.println("[MockSlack] Sent: " + message);
    }
    
    public boolean received(String content) {
        return messages.stream().anyMatch(m -> m.contains(content));
    }
}
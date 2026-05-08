package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackPort implements SlackPort {

    // Captured messages to verify behavior in tests
    public final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendMessage(String message) {
        // Simulate sending by storing the message
        this.sentMessages.add(message);
    }

    public void clear() {
        this.sentMessages.clear();
    }
}
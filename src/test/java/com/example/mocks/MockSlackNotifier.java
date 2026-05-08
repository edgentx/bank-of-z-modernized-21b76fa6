package com.example.mocks;

import com.example.ports.SlackNotifierPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotifierPort.
 * Stores sent messages to verify behavior in tests without real I/O.
 */
public class MockSlackNotifier implements SlackNotifierPort {

    public final List<String> sentMessages = new ArrayList<>();
    public boolean failOnSend = false;

    @Override
    public void send(String messageBody) {
        if (failOnSend) {
            throw new RuntimeException("Simulated Slack failure");
        }
        sentMessages.add(messageBody);
    }

    public boolean containsLink(String url) {
        return sentMessages.stream().anyMatch(msg -> msg.contains(url));
    }
}

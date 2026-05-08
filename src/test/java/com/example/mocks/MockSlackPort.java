package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 */
public class MockSlackPort implements SlackPort {
    public final List<String> sentMessages = new ArrayList<>();
    public String lastChannel;

    @Override
    public void sendNotification(String channel, String messageBody) {
        this.lastChannel = channel;
        this.sentMessages.add(messageBody);
    }

    public boolean wasUrlSentTo(String expectedUrl, String channel) {
        return sentMessages.stream()
                .filter(msg -> msg.contains(expectedUrl))
                .anyMatch(msg -> channel.equals(this.lastChannel));
    }
}

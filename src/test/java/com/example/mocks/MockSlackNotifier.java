package com.example.mocks;

import com.example.domain.shared.SlackMessage;
import com.example.ports.SlackNotifierPort;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory mock implementation of SlackNotifierPort for testing.
 * Captures messages sent to allow assertions on body content and URL presence.
 */
public class MockSlackNotifier implements SlackNotifierPort {
    private final List<SlackMessage> sentMessages = new ArrayList<>();

    @Override
    public void send(SlackMessage message) {
        this.sentMessages.add(message);
    }

    public List<SlackMessage> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }
}
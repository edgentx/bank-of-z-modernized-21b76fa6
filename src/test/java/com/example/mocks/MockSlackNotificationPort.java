package com.example.mocks;

import com.example.domain.validation.model.SlackMessageBody;
import com.example.domain.validation.port.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock Adapter for Slack Notification Port.
 * Captures messages sent during the workflow for assertions.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<SlackMessageBody> sentMessages = new ArrayList<>();

    @Override
    public void send(SlackMessageBody body) {
        System.out.println("[MockSlack] Sending: " + body.value());
        sentMessages.add(body);
    }

    public SlackMessageBody getLastMessage() {
        if (sentMessages.isEmpty()) return null;
        return sentMessages.get(sentMessages.size() - 1);
    }

    public List<SlackMessageBody> getAllMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void reset() {
        sentMessages.clear();
    }
}

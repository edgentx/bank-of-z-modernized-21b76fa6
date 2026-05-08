package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackNotificationPort implements SlackNotificationPort {
    public final List<String> sentMessages = new ArrayList<>();
    public boolean shouldFail = false;

    @Override
    public void sendNotification(String message) {
        if (shouldFail) throw new RuntimeException("Slack API Unavailable");
        sentMessages.add(message);
    }

    public boolean containsUrl(String url) {
        return sentMessages.stream().anyMatch(msg -> msg.contains(url));
    }

    public void reset() {
        sentMessages.clear();
        shouldFail = false;
    }
}

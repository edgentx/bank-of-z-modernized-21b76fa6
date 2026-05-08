package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack Notifications.
 * Captures messages in memory for assertion in tests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<String> postedMessages = new ArrayList<>();
    public String lastChannelId;
    public boolean shouldFail = false;

    @Override
    public boolean sendMessage(String channelId, String messageBody) {
        this.lastChannelId = channelId;
        this.postedMessages.add(messageBody);
        return !shouldFail;
    }

    public void reset() {
        postedMessages.clear();
        lastChannelId = null;
        shouldFail = false;
    }
}

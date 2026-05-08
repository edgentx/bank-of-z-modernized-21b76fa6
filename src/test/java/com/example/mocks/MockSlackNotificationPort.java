package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<String> postedMessages = new ArrayList<>();
    public String lastChannelId;
    private boolean shouldFail = false;

    @Override
    public void postMessage(String channelId, String messageBody) {
        if (shouldFail) {
            throw new RuntimeException("Slack API unavailable (simulated)");
        }
        if (channelId == null || messageBody == null) {
            throw new IllegalArgumentException("ChannelId and MessageBody cannot be null");
        }
        this.lastChannelId = channelId;
        this.postedMessages.add(messageBody);
    }

    @Override
    public void postToDefaultChannel(String messageBody) {
        postMessage("C_DEFAULT_ISSUES", messageBody);
    }

    public void reset() {
        postedMessages.clear();
        lastChannelId = null;
    }

    public void setShouldFail(boolean flag) {
        this.shouldFail = flag;
    }

    public boolean wasCalled() {
        return !postedMessages.isEmpty();
    }
}
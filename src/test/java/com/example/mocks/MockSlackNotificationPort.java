package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<String> postedBodies = new ArrayList<>();
    public final List<String> postedChannels = new ArrayList<>();

    @Override
    public boolean postMessage(String channelId, String messageBody) {
        this.postedChannels.add(channelId);
        this.postedBodies.add(messageBody);
        return true;
    }

    public void reset() {
        postedBodies.clear();
        postedChannels.clear();
    }
}
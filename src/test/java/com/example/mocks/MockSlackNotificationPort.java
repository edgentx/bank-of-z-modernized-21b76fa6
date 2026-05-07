package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> postedMessages = new ArrayList<>();
    private String lastChannelId;

    @Override
    public void postMessage(String channelId, String messageBody) {
        this.lastChannelId = channelId;
        this.postedMessages.add(messageBody);
    }

    public List<String> getPostedMessages() {
        return new ArrayList<>(postedMessages);
    }

    public String getLastMessageBody() {
        return postedMessages.isEmpty() ? null : postedMessages.get(postedMessages.size() - 1);
    }

    public String getLastChannelId() {
        return lastChannelId;
    }

    public void reset() {
        postedMessages.clear();
        lastChannelId = null;
    }
}

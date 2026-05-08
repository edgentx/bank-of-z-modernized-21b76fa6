package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackNotificationPort implements SlackNotificationPort {
    public List<String> sentMessages = new ArrayList<>();
    public String lastChannel;

    @Override
    public void sendNotification(String channel, String message) {
        this.lastChannel = channel;
        this.sentMessages.add(message);
    }

    public boolean wasUrlSent(String url) {
        return sentMessages.stream().anyMatch(msg -> msg.contains(url));
    }
}

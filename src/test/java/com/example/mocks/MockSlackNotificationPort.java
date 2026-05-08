package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackNotificationPort implements SlackNotificationPort {
    
    public final List<String> sentMessages = new ArrayList<>();
    public String lastChannel;

    @Override
    public void sendNotification(String channel, String message) {
        this.lastChannel = channel;
        this.sentMessages.add(message);
    }

    public boolean wasUrlSentInSlack(String url) {
        return sentMessages.stream().anyMatch(msg -> msg.contains(url));
    }
}

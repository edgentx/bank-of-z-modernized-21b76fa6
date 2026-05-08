package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<String> sentBodies = new ArrayList<>();
    public final List<String> channels = new ArrayList<>();

    @Override
    public void sendNotification(String channel, String body) {
        this.channels.add(channel);
        this.sentBodies.add(body);
    }

    public boolean bodyContains(String text) {
        return sentBodies.stream().anyMatch(b -> b != null && b.contains(text));
    }
}
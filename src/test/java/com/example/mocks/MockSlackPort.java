package com.example.mocks;

import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MockSlackPort implements SlackPort {
    private final List<String> sentBodies = new ArrayList<>();

    @Override
    public void sendNotification(String url) {
        // Simulate Slack API call
        sentBodies.add(url);
    }

    public String getLastSentBody() {
        return sentBodies.isEmpty() ? null : sentBodies.get(sentBodies.size() - 1);
    }
}
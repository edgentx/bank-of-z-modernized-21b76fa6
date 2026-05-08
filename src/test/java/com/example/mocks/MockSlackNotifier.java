package com.example.mocks;

import com.example.ports.SlackNotifierPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for SlackNotifierPort.
 * Stores messages in memory for verification during tests.
 */
@Component
public class MockSlackNotifier implements SlackNotifierPort {

    private final List<String> notifications = new ArrayList<>();

    @Override
    public void notify(String body) {
        // Simulate sending the message by storing it
        this.notifications.add(body);
        System.out.println("[MockSlack] Captured notification: " + body);
    }

    public boolean wasNotifyCalled() {
        return !notifications.isEmpty();
    }

    public String getLastBody() {
        if (notifications.isEmpty()) return null;
        return notifications.get(notifications.size() - 1);
    }

    public void reset() {
        notifications.clear();
    }
}

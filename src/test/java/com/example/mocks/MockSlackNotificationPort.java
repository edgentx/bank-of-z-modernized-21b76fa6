package com.example.mocks;

import com.example.domain.defect.service.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock Slack port for testing message content.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {
    public final List<String> messages = new ArrayList<>();
    public String lastChannel;

    @Override
    public void sendNotification(String channel, String message) {
        this.lastChannel = channel;
        this.messages.add(message);
    }
}

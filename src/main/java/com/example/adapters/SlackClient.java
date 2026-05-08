package com.example.adapters;

import com.example.ports.NotificationPort;

public class SlackClient implements NotificationPort {
    @Override
    public void send(String channel, String message) {
        // Actual Slack implementation would go here (HTTP POST)
        System.out.println("Sending to " + channel + ": " + message);
    }
}

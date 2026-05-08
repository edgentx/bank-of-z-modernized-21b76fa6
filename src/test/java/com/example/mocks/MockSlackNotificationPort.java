package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory mock for Slack notifications.
 * Records payloads for assertion in tests.
 */
@Component
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> payloads = new ArrayList<>();

    @Override
    public void send(String payload) {
        this.payloads.add(payload);
    }

    @Override
    public String getLastSentPayload() {
        if (payloads.isEmpty()) return null;
        return payloads.get(payloads.size() - 1);
    }

    public void clear() {
        payloads.clear();
    }
}
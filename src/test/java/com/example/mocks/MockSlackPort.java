package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Records payloads to verify behavior without calling I/O.
 */
public class MockSlackPort implements SlackPort {
    public final List<CapturedCall> calls = new ArrayList<>();

    @Override
    public void sendNotification(String webhookUrl, String jsonPayload) {
        calls.add(new CapturedCall(webhookUrl, jsonPayload));
    }

    public record CapturedCall(String url, String payload) {}
}

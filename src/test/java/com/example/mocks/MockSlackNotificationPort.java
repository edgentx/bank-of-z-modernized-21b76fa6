package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

/**
 * Mock adapter for Slack notifications.
 * In-memory implementation to avoid real network calls during tests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final SlackNotificationPort mockDelegate;

    public MockSlackNotificationPort() {
        this.mockDelegate = Mockito.mock(SlackNotificationPort.class);
        // By default, do nothing (void method)
    }

    @Override
    public void postMessage(String channel, String body) {
        mockDelegate.postMessage(channel, body);
    }

    public SlackNotificationPort getMock() {
        return mockDelegate;
    }
}

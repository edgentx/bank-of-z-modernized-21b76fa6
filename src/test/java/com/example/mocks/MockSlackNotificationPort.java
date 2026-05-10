package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for the Slack Notification Port.
 * Used in TDD/Unit tests to verify that the application logic
 * constructs the correct message payload without actually calling the external Slack API.
 */
@Component
public class MockSlackNotificationPort implements SlackNotificationPort {

    private boolean called = false;
    private String lastChannel;
    private String lastMessageBody;
    private final List<CapturedCall> callHistory = new ArrayList<>();

    @Override
    public void send(String channel, String messageBody) {
        this.called = true;
        this.lastChannel = channel;
        this.lastMessageBody = messageBody;
        this.callHistory.add(new CapturedCall(channel, messageBody));
        
        // Intentionally do NOT perform real I/O
        System.out.println("[MOCK] Slack sent to " + channel + ": " + messageBody);
    }

    public boolean wasCalled() {
        return called;
    }

    public String getLastMessageBody() {
        return lastMessageBody;
    }

    public String getLastChannel() {
        return lastChannel;
    }

    public void reset() {
        this.called = false;
        this.lastChannel = null;
        this.lastMessageBody = null;
        this.callHistory.clear();
    }

    private record CapturedCall(String channel, String body) {}
}

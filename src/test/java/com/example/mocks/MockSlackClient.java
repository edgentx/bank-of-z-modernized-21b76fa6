package com.example.mocks;

import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

/**
 * Mock Slack Adapter for testing.
 * Captures message content to verify the defect reporting logic.
 */
@Component
public class MockSlackClient implements SlackPort {

    private boolean sendMessageCalled = false;
    private String lastMessageBody;
    private String lastChannel;

    @Override
    public void sendMessage(String channel, String text) {
        this.sendMessageCalled = true;
        this.lastChannel = channel;
        this.lastMessageBody = text;
    }

    public boolean wasSendMessageCalled() {
        return sendMessageCalled;
    }

    public String getLastMessageBody() {
        return lastMessageBody;
    }

    public String getLastChannel() {
        return lastChannel;
    }

    public void reset() {
        this.sendMessageCalled = false;
        this.lastMessageBody = null;
        this.lastChannel = null;
    }
}

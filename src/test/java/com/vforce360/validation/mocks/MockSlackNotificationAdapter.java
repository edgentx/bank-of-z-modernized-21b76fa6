package com.vforce360.validation.mocks;

import com.vforce360.validation.ports.SlackNotificationPort;
import com.vforce360.validation.ports.SlackMessagePayload;
import org.springframework.stereotype.Component;

/**
 * Mock implementation of SlackNotificationPort.
 * This is used in the Spring Context if real I/O needs to be suppressed completely,
 * though Mockito is preferred for unit tests.
 */
@Component
public class MockSlackNotificationAdapter implements SlackNotificationPort {

    private SlackMessagePayload lastPayload;
    private boolean sendMessageCalled = false;

    @Override
    public void sendMessage(SlackMessagePayload payload) {
        this.lastPayload = payload;
        this.sendMessageCalled = true;
        // No real HTTP call is made
    }

    public SlackMessagePayload getLastPayload() {
        return lastPayload;
    }

    public boolean isSendMessageCalled() {
        return sendMessageCalled;
    }
    
    public void reset() {
        this.lastPayload = null;
        this.sendMessageCalled = false;
    }
}

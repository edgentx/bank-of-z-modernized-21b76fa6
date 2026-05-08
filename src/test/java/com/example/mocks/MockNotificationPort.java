package com.example.mocks;

import com.example.ports.NotificationPort;

/**
 * Mock implementation of {@link com.example.ports.NotificationPort} for testing.
 * Captures the last sent body for verification in tests.
 */
public class MockNotificationPort implements NotificationPort {

    private String lastBody;
    private String lastSubject;
    private String lastRecipient;
    private boolean sendReturnValue = true;

    @Override
    public boolean sendNotification(String recipient, String subject, String body) {
        this.lastRecipient = recipient;
        this.lastSubject = subject;
        this.lastBody = body;
        return sendReturnValue;
    }

    public String getLastBody() {
        return lastBody;
    }

    public String getLastSubject() {
        return lastSubject;
    }

    public String getLastRecipient() {
        return lastRecipient;
    }

    public void setSendReturnValue(boolean val) {
        this.sendReturnValue = val;
    }
}

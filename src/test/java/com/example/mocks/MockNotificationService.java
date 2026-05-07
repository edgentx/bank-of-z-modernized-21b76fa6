package com.example.mocks;

import com.example.domain.defect.ports.NotificationService;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of NotificationService for testing.
 * Captures messages to memory to verify behavior without external I/O.
 */
public class MockNotificationService implements NotificationService {
    
    public final List<String> sentMessages = new ArrayList<>();
    private boolean shouldFail = false;

    @Override
    public void sendDefectNotification(String content) {
        if (shouldFail) {
            throw new NotificationException("Mock failure triggered");
        }
        sentMessages.add(content);
    }

    public void reset() {
        sentMessages.clear();
        shouldFail = false;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}

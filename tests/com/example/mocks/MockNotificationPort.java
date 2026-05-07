package com.example.mocks;

import com.example.ports.NotificationPort;
import com.example.vforce.shared.ReportDefectCommand;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of NotificationPort for testing.
 * Captures sent messages to verify content.
 */
public class MockNotificationPort implements NotificationPort {
    public final List<ReportDefectCommand> sentNotifications = new ArrayList<>();
    
    @Override
    public void notifyChannel(ReportDefectCommand command) {
        // Simulate Slack latency or queueing
        this.sentNotifications.add(command);
    }
    
    public boolean wasNotified() {
        return !sentNotifications.isEmpty();
    }
    
    public ReportDefectCommand getLastNotification() {
        if (sentNotifications.isEmpty()) throw new IllegalStateException("No notifications sent");
        return sentNotifications.get(sentNotifications.size() - 1);
    }
}
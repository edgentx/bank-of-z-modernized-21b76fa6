package com.example.mocks;

import com.example.domain.notification.model.NotificationAggregate;
import com.example.ports.NotificationPort;

public class MockNotificationPort implements NotificationPort {
    private boolean called = false;
    private NotificationAggregate lastNotification;

    @Override
    public void send(NotificationAggregate notification) {
        this.called = true;
        this.lastNotification = notification;
    }

    public boolean isCalled() {
        return called;
    }

    public NotificationAggregate getLastNotification() {
        return lastNotification;
    }
}
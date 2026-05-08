package com.example.mocks;

import com.example.domain.notification.model.NotificationAggregate;
import com.example.vforce.adapter.SlackNotificationAdapter;
import java.util.ArrayList;
import java.util.List;

public class MockSlackNotificationAdapter extends SlackNotificationAdapter {
    public final List<NotificationAggregate> sentNotifications = new ArrayList<>();

    @Override
    public void send(NotificationAggregate aggregate) {
        sentNotifications.add(aggregate);
    }

    public String getLastMessageBody() {
        if (sentNotifications.isEmpty()) return null;
        // Assuming the aggregate has a way to get content, or we inspect state via reflection
        // For this test, we'll rely on the Workflow logic to capture the formatted string
        return "Mock implementation";
    }
}
package com.example.vforce.adapter;

import com.example.domain.notification.model.NotificationAggregate;
import org.springframework.stereotype.Component;

@Component
public class SlackNotificationAdapter {

    public void send(NotificationAggregate aggregate) {
        // Implementation currently missing or throwing error
        throw new UnsupportedOperationException("Slack notification not implemented");
    }
}
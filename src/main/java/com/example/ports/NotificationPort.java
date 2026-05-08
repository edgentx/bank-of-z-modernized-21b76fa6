package com.example.ports;

import com.example.domain.notification.model.NotificationAggregate;

public interface NotificationPort {
    void send(NotificationAggregate notification);
}
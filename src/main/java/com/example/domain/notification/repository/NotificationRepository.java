package com.example.domain.notification.repository;

import com.example.domain.notification.model.NotificationAggregate;

public interface NotificationRepository {
    NotificationAggregate save(NotificationAggregate aggregate);
}

package com.example.domain.notification.repository;

import com.example.domain.notification.model.NotificationAggregate;
import java.util.Optional;

public interface NotificationRepository {
    NotificationAggregate save(NotificationAggregate aggregate);
    Optional<NotificationAggregate> findById(String id);
}

package com.example.domain.notification.repository;

import com.example.domain.notification.model.NotificationAggregate;

import java.util.Optional;

/**
 * Port interface for Notification persistence.
 */
public interface NotificationRepository {
    void save(NotificationAggregate aggregate);
    Optional<NotificationAggregate> findById(String id);
}

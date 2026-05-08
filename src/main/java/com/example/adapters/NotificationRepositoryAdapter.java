package com.example.adapters;

import com.example.domain.notification.model.NotificationAggregate;
import com.example.domain.notification.repository.NotificationRepository;
import org.springframework.stereotype.Component;

@Component
public class NotificationRepositoryAdapter implements NotificationRepository {
    @Override
    public NotificationAggregate save(NotificationAggregate aggregate) {
        // Persist to DB
        return aggregate;
    }
}

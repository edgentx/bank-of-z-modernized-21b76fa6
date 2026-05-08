package com.example.mocks;

import com.example.domain.notification.model.NotificationAggregate;
import com.example.domain.notification.repository.NotificationRepository;
import java.util.HashMap;
    import java.util.Map;
import java.util.Optional;

public class MockNotificationRepository implements NotificationRepository {
    private final Map<String, NotificationAggregate> store = new HashMap<>();

    @Override
    public NotificationAggregate save(NotificationAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<NotificationAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}

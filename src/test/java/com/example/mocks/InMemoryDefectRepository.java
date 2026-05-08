package com.example.mocks;

import com.example.domain.validation.model.DefectAggregate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * In-memory repository for DefectAggregate.
 */
public class InMemoryDefectRepository {
    private final Map<String, DefectAggregate> store = new HashMap<>();

    public DefectAggregate save(DefectAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    public Optional<DefectAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public DefectAggregate createNew() {
        String id = UUID.randomUUID().toString();
        return new DefectAggregate(id);
    }
}

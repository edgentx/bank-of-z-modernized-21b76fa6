package com.example.mocks;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory repository for LegacyTransactionRoute.
 * Located in tests/ directory as per DDD/hexagonal layout.
 */
public class InMemoryLegacyTransactionRouteRepository {
    private final Map<String, LegacyTransactionRoute> store = new HashMap<>();

    public void save(LegacyTransactionRoute aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    public LegacyTransactionRoute findById(String id) {
        return store.get(id);
    }

    public void delete(String id) {
        store.remove(id);
    }
}

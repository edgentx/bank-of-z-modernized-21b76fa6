package com.example.mocks;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.repository.DefectRepository;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of DefectRepository for testing.
 */
public class MockDefectRepository implements DefectRepository {
    private final Map<String, DefectAggregate> store = new HashMap<>();

    @Override
    public void save(DefectAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public DefectAggregate findById(String id) {
        return store.get(id);
    }
}

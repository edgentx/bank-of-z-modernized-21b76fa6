package com.example.mocks;

import com.example.defect.domain.DefectAggregate;
import com.example.defect.repository.DefectRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of DefectRepository for testing.
 */
public class InMemoryDefectRepository implements DefectRepository {

    private final Map<String, DefectAggregate> store = new HashMap<>();

    @Override
    public void save(DefectAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public DefectAggregate findById(String defectId) {
        return store.get(defectId);
    }
    
    public Optional<DefectAggregate> findOptionalById(String defectId) {
        return Optional.ofNullable(store.get(defectId));
    }
}

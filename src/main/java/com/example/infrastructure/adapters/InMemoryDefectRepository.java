package com.example.infrastructure.adapters;

import com.example.domain.defect.model.DefectAggregate;
import com.example.defect.repository.DefectRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of the DefectRepository.
 * Used for testing and rapid prototyping. Stores aggregates in a concurrent map.
 */
@Component
public class InMemoryDefectRepository implements DefectRepository {

    private final Map<String, DefectAggregate> store = new HashMap<>();

    @Override
    public void save(DefectAggregate aggregate) {
        // In a real DB adapter, we would map the Aggregate state to tables.
        // Here we just keep the object reference.
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<DefectAggregate> findById(String defectId) {
        return Optional.ofNullable(store.get(defectId));
    }
}

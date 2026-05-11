package com.example.adapters;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.repository.DefectRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Persistence adapter for the Defect Aggregate.
 * Implements the repository interface defined in the domain layer.
 */
@Component
public class DefectRepositoryAdapter implements DefectRepository {

    private final Map<String, DefectAggregate> store = new HashMap<>();

    @Override
    public DefectAggregate save(DefectAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<DefectAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}

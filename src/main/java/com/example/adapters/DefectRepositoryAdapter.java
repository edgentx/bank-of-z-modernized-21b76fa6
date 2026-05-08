package com.example.adapters;

import com.example.domain.defect.model.DefectAggregate;
import com.example.ports.DefectRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of the DefectRepository.
 * NOTE: In a real production environment, this would interact with MongoDB (as per VForce360 standards)
 * or DB2. For the scope of this defect fix and unit testing, an in-memory map is sufficient.
 */
@Component
public class DefectRepositoryAdapter implements DefectRepository {

    private final Map<String, DefectAggregate> store = new HashMap<>();

    @Override
    public void save(DefectAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<DefectAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}

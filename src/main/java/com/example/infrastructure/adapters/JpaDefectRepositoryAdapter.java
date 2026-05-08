package com.example.infrastructure.adapters;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.port.DefectRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * JPA Adapter implementation for Defect Repository.
 */
@Component
public class JpaDefectRepositoryAdapter implements DefectRepository {

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

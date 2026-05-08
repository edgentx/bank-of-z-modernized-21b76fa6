package com.example.adapters;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.repository.DefectRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory repository adapter for DefectAggregate.
 * Uses a simple HashMap for persistence. In a real scenario, this would interface with DB2.
 */
@Repository
public class DefectRepositoryImpl implements DefectRepository {

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

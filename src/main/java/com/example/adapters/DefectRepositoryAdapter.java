package com.example.adapters;

import com.example.domain.vforce360.model.DefectAggregate;
import com.example.ports.DefectRepositoryPort;
import org.springframework.stereotype.Repository;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory adapter for DefectRepositoryPort.
 * Useful for rapid prototyping or if persistence is not yet required for this context.
 */
@Repository
public class DefectRepositoryAdapter implements DefectRepositoryPort {
    
    private final ConcurrentHashMap<String, DefectAggregate> store = new ConcurrentHashMap<>();

    @Override
    public void save(DefectAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public DefectAggregate findById(String defectId) {
        return store.get(defectId);
    }
}

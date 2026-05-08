package com.example.adapters;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.repository.DefectRepository;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * In-Memory implementation of DefectRepository.
 * suitable for the Green phase without a real database connection.
 */
@Component
public class DefectRepositoryImpl implements DefectRepository {

    private final Map<String, DefectAggregate> store = new HashMap<>();

    @Override
    public DefectAggregate save(DefectAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public DefectAggregate findById(String defectId) {
        return store.get(defectId);
    }
}

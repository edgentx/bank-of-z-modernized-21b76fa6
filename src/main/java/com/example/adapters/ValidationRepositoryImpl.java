package com.example.adapters;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.repository.DefectRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of DefectRepository.
 * Placeholder for a real persistent store (e.g., MongoDB/DB2).
 */
@Component
public class ValidationRepositoryImpl implements DefectRepository {

    private final Map<String, DefectAggregate> store = new HashMap<>();

    @Override
    public void save(DefectAggregate defect) {
        store.put(defect.id(), defect);
    }

    @Override
    public DefectAggregate findById(String defectId) {
        return store.get(defectId);
    }
}

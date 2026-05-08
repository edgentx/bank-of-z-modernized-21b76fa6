package com.example.adapters;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.repository.DefectRepository;
import org.springframework.stereotype.Repository;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Repository
public class ValidationRepositoryImpl implements DefectRepository {
    private final Map<String, DefectAggregate> store = new ConcurrentHashMap<>();

    @Override
    public void save(DefectAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public DefectAggregate findById(String defectId) {
        return store.get(defectId);
    }
}

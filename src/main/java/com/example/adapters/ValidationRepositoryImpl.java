package com.example.adapters;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

// In-memory implementation for testing/dev purposes
@Component
public class ValidationRepositoryImpl implements DefectRepository {

    private final Map<String, DefectAggregate> store = new HashMap<>();

    @Override
    public DefectAggregate save(DefectAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public DefectAggregate findById(String id) {
        return store.get(id);
    }
}

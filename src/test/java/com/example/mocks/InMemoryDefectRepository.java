package com.example.mocks;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.repository.DefectRepository;
import java.util.HashMap;
import java.util.Map;

public class InMemoryDefectRepository implements DefectRepository {
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

package com.example.adapters;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.repository.DefectRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class DefectRepositoryAdapter implements DefectRepository {

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

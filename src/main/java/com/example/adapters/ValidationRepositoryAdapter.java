package com.example.adapters;

import com.example.domain.validation.Validation;
import com.example.domain.validation.repository.ValidationRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class ValidationRepositoryAdapter implements ValidationRepository {

    private final Map<String, Validation> store = new HashMap<>();

    @Override
    public Validation save(Validation validation) {
        store.put(validation.getId(), validation);
        return validation;
    }

    @Override
    public Optional<Validation> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}

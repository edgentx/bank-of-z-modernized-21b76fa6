package com.example.adapters;

import com.example.domain.validation.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of ValidationRepository for demonstration/testing within the app context.
 * In a real scenario, this would be @Repository and extend MongoRepository<JpaEntity, String>.
 * Using a simple Map to satisfy the repository contract without needing a running DB for the logic verification.
 */
public class MongoValidationRepositoryAdapter implements ValidationRepository {

    private final Map<String, ValidationAggregate> store = new HashMap<>();

    @Override
    public Optional<ValidationAggregate> findById(String defectId) {
        return Optional.ofNullable(store.get(defectId));
    }

    @Override
    public void save(ValidationAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }
}

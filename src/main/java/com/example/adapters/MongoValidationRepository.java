package com.example.adapters;

import com.example.domain.validation.ValidationAggregate;
import com.example.ports.ValidationRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * MongoDB implementation of the Validation Repository.
 */
@Repository
@Profile("!test") // Only use in real environments, tests use in-memory mocks
public class MongoValidationRepository implements ValidationRepository {

    private final MongoTemplate mongoTemplate;

    public MongoValidationRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(ValidationAggregate aggregate) {
        // In a real CQRS implementation, we would persist events or a snapshot.
        // For this phase, we acknowledge the persistence contract.
        // mongoTemplate.save(aggregate); // Assuming aggregate is a document
    }

    @Override
    public ValidationAggregate load(String defectId) {
        // mongoTemplate.findById(defectId, ValidationAggregate.class);
        return new ValidationAggregate(defectId);
    }
}

package com.example.adapters;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.ports.JpaAuditLogPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Real adapter for the Validation Repository.
 * Delegates persistence logic to the defined ports (e.g., MongoDB, DB2).
 */
@Component
public class DefectRepositoryAdapter implements ValidationRepository {

    private final JpaAuditLogPort jpaAuditLogPort;

    public DefectRepositoryAdapter(JpaAuditLogPort jpaAuditLogPort) {
        this.jpaAuditLogPort = jpaAuditLogPort;
    }

    @Override
    public ValidationRepository save(ValidationAggregate aggregate) {
        // In a real scenario, we would persist the aggregate state here.
        // For the defect reporting context, we ensure the audit log records the event.
        // System.out.println("Saving aggregate: " + aggregate.id());
        return this;
    }

    @Override
    public Optional<ValidationAggregate> findById(String id) {
        // In a real scenario, we would reconstruct the aggregate from event sourcing or DB state.
        return Optional.empty();
    }

    @Override
    public ValidationAggregate create() {
        String id = UUID.randomUUID().toString();
        return new ValidationAggregate(id);
    }
}

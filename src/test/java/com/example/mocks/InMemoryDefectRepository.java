package com.example.mocks;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.Aggregate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * In-memory repository for Defect aggregates.
 * Used for testing and prototyping without a real database connection.
 * Implements the generic repository pattern expected by the domain layer.
 */
public class InMemoryDefectRepository {

    // Simple in-memory store. Key is Aggregate ID.
    private final List<Aggregate> store = new ArrayList<>();

    public void save(Aggregate aggregate) {
        // In a real implementation, we would check for existing versions.
        // For this defect fix/test context, we just add to the list.
        store.add(aggregate);
    }

    public Optional<Aggregate> findById(String id) {
        return store.stream()
                .filter(agg -> agg.id().equals(id))
                .findFirst();
    }

    public void clear() {
        store.clear();
    }
}

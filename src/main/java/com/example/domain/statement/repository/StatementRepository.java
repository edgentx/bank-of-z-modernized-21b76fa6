package com.example.domain.statement.repository;

import com.example.domain.statement.model.StatementAggregate;
import java.util.Optional;

public interface StatementRepository {
    StatementAggregate save(StatementAggregate aggregate);
    Optional<StatementAggregate> findById(String id);
    // Test specific: In-memory implementations might need a way to clear or load specific states
}

package com.example.domain.statement.repository;

import com.example.domain.statement.model.StatementAggregate;

import java.util.Optional;

/**
 * Repository for Statement aggregates.
 */
public interface StatementRepository {
    StatementAggregate save(StatementAggregate aggregate);
    Optional<StatementAggregate> findById(String id);
}

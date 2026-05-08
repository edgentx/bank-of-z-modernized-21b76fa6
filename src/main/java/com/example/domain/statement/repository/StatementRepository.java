package com.example.domain.statement.repository;

import com.example.domain.statement.model.StatementAggregate;
import java.util.Optional;

/**
 * Repository interface for Statement Aggregate.
 * S-8: Implement GenerateStatementCmd.
 */
public interface StatementRepository {
    StatementAggregate save(StatementAggregate aggregate);
    Optional<StatementAggregate> findById(String id);
}

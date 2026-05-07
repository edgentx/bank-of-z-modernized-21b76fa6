package com.example.domain.statement.repository;

import com.example.domain.statement.model.StatementAggregate;
import java.util.Optional;

public interface StatementRepository {
    void save(StatementAggregate aggregate);
    Optional<StatementAggregate> findById(String id);
}

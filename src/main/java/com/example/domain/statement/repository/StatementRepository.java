package com.example.domain.statement.repository;

import com.example.domain.statement.model.StatementAggregate;

public interface StatementRepository {
    StatementAggregate save(StatementAggregate aggregate);
    StatementAggregate findById(String id);
}

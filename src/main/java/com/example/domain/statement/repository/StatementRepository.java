package com.example.domain.statement.repository;

import com.example.domain.statement.model.StatementAggregate;

public interface StatementRepository {
    StatementAggregate findById(String id);
    void save(StatementAggregate aggregate);
}

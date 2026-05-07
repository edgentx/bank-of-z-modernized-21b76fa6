package com.example.domain.statement.repository;

import com.example.domain.statement.model.StatementAggregate;
import java.util.Optional;

public interface StatementRepository {
  Optional<StatementAggregate> findById(String statementId);
  void save(StatementAggregate aggregate);
}

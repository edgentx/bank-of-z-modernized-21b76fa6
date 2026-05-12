package com.example.infrastructure.mongo.statement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatementMongoDataRepository extends MongoRepository<StatementDocument, String> {
  Page<StatementDocument> findByAccountNumber(String accountNumber, Pageable pageable);
}

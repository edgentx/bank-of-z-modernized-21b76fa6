package com.example.infrastructure.mongo.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionMongoDataRepository extends MongoRepository<TransactionDocument, String> {
  Page<TransactionDocument> findByAccountId(String accountId, Pageable pageable);
  List<TransactionDocument> findByAccountIdAndKind(String accountId, String kind);
}

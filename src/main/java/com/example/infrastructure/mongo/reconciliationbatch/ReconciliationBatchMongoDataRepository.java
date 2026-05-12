package com.example.infrastructure.mongo.reconciliationbatch;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReconciliationBatchMongoDataRepository
    extends MongoRepository<ReconciliationBatchDocument, String> {
}

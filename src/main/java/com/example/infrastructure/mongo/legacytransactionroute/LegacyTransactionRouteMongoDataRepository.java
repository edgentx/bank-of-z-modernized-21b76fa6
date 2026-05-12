package com.example.infrastructure.mongo.legacytransactionroute;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegacyTransactionRouteMongoDataRepository
    extends MongoRepository<LegacyTransactionRouteDocument, String> {
}

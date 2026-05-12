package com.example.infrastructure.mongo.datasynccheckpoint;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataSyncCheckpointMongoDataRepository
    extends MongoRepository<DataSyncCheckpointDocument, String> {
}

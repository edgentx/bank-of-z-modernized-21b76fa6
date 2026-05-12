package com.example.infrastructure.mongo.screenmap;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScreenMapMongoDataRepository extends MongoRepository<ScreenMapDocument, String> {
}

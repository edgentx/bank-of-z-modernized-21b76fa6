package com.example.infrastructure.mongo.tellersession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TellerSessionMongoDataRepository extends MongoRepository<TellerSessionDocument, String> {
  Page<TellerSessionDocument> findByStatus(String status, Pageable pageable);
}

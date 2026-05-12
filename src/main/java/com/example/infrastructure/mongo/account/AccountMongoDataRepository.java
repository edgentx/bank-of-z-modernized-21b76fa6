package com.example.infrastructure.mongo.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data Mongo interface for {@link AccountDocument}.
 *
 * Custom finders (S-28 AC #4):
 *   - findByCustomerId: page through all accounts owned by a customer.
 *   - findByStatus: support Active-/Closed-filtered listings.
 */
@Repository
public interface AccountMongoDataRepository extends MongoRepository<AccountDocument, String> {
  Page<AccountDocument> findByCustomerId(String customerId, Pageable pageable);
  List<AccountDocument> findByStatus(String status);
}

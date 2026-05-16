package com.example.infrastructure.mongo.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data Mongo interface for {@link CustomerDocument}.
 *
 * Custom finders (S-28 AC #4 — "custom query methods support required filtering
 * and pagination"):
 *   - findByEmail: lookup-by-email is the canonical operator path.
 *   - findBySortCode: branch-scoped scans, supports paging.
 */
@Repository
public interface CustomerMongoDataRepository extends MongoRepository<CustomerDocument, String> {
  Optional<CustomerDocument> findByEmail(String email);
  Page<CustomerDocument> findBySortCode(String sortCode, Pageable pageable);
  Page<CustomerDocument> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);
  long countByEnrolledTrue();
}

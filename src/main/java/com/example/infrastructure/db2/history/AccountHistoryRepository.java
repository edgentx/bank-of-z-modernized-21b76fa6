package com.example.infrastructure.db2.history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for legacy DB2 account-history rows (S-29).
 *
 * Read paths:
 *   - findByCustomerId — list a customer's pre-migration accounts on the
 *     teller customer-detail view,
 *   - findByStatus     — drive closure-rate dashboards and orphan-account
 *     reconciliation jobs.
 */
@Repository
public interface AccountHistoryRepository
    extends JpaRepository<AccountHistoryEntity, String> {

  List<AccountHistoryEntity> findByCustomerId(String customerId);

  List<AccountHistoryEntity> findByStatus(String status);
}

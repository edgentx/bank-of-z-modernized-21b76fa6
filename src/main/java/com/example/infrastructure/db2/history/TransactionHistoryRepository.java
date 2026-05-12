package com.example.infrastructure.db2.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Spring Data JPA repository for legacy DB2 transaction-history rows (S-29).
 *
 * Read-paths the modernized application needs against legacy data:
 *   - per-account paginated history (account-detail screens, statement runs),
 *   - by-kind slice (deposit/withdrawal totals for reconciliation),
 *   - posted-at range scan (date-bounded reporting).
 */
@Repository
public interface TransactionHistoryRepository
    extends JpaRepository<TransactionHistoryEntity, String> {

  Page<TransactionHistoryEntity> findByAccountId(String accountId, Pageable pageable);

  List<TransactionHistoryEntity> findByKind(String kind);

  @Query("""
      SELECT t FROM TransactionHistoryEntity t
      WHERE t.accountId = :accountId
        AND t.postedAt BETWEEN :from AND :to
      ORDER BY t.postedAt ASC
      """)
  List<TransactionHistoryEntity> findAccountActivityBetween(
      @Param("accountId") String accountId,
      @Param("from") Instant from,
      @Param("to") Instant to);
}

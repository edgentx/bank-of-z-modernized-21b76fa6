package com.example.infrastructure.db2.history;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BANK S-29 — DB2 history repositories integration test.
 *
 * Exercises the full JPA stack against an embedded H2 instance running in
 * DB2-compatibility mode:
 *   - Flyway's V1.0.0 migration is run by Spring Boot at context startup,
 *   - the schema is validated against the {@link TransactionHistoryEntity}
 *     and {@link AccountHistoryEntity} JPA mappings,
 *   - Spring Data finder methods are exercised end-to-end via the embedded
 *     EntityManager (no Hibernate mocking).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:history;MODE=DB2;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=validate",
    "spring.flyway.enabled=true",
    "spring.flyway.locations=classpath:db/migration",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,"
        + "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,"
        + "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration"
})
class HistoryRepositoriesIntegrationTest {

  /**
   * @DataJpaTest scans for @Entity classes and @EnableJpaRepositories from the
   * boot class. The production application has no main class yet (S-29 ships
   * the persistence layer ahead of the bootstrap), so we provide a minimal
   * inner config to anchor component scanning at the db2.history package.
   */
  @Configuration
  @EnableAutoConfiguration(exclude = {
      // Mongo is on the classpath but irrelevant for this slice.
  })
  @ComponentScan(basePackages = "com.example.infrastructure.db2.history")
  @EnableJpaRepositories(basePackages = "com.example.infrastructure.db2.history")
  static class TestConfig {}

  @Autowired private TestEntityManager em;
  @Autowired private TransactionHistoryRepository txRepo;
  @Autowired private AccountHistoryRepository acctRepo;

  @Test
  void flywayCreatesSchemaAndJpaCanWriteAndReadTransactionHistory() {
    TransactionHistoryEntity row = new TransactionHistoryEntity(
        "tx-h-1", "acct-1", "deposit",
        new BigDecimal("250.0000"), "USD",
        Instant.parse("2025-01-15T10:00:00Z"),
        false, "DB2");
    em.persistAndFlush(row);
    em.clear();

    TransactionHistoryEntity loaded = txRepo.findById("tx-h-1").orElseThrow();
    assertEquals("acct-1", loaded.getAccountId());
    assertEquals("deposit", loaded.getKind());
    assertEquals(new BigDecimal("250.0000"), loaded.getAmount());
    assertEquals("USD", loaded.getCurrency());
    assertFalse(loaded.isReversed());
    assertEquals("DB2", loaded.getLegacySource());
  }

  @Test
  void findByAccountIdReturnsPagedHistory() {
    persistTx("tx-a", "acct-2", "deposit", "100", Instant.parse("2025-02-01T00:00:00Z"));
    persistTx("tx-b", "acct-2", "withdrawal", "30", Instant.parse("2025-02-02T00:00:00Z"));
    persistTx("tx-c", "acct-3", "deposit", "999", Instant.parse("2025-02-03T00:00:00Z"));
    em.flush();
    em.clear();

    Page<TransactionHistoryEntity> page = txRepo.findByAccountId("acct-2", PageRequest.of(0, 10));
    assertEquals(2, page.getTotalElements());
  }

  @Test
  void findAccountActivityBetweenScopesToDateWindow() {
    persistTx("tx-d", "acct-4", "deposit", "10", Instant.parse("2025-03-01T00:00:00Z"));
    persistTx("tx-e", "acct-4", "deposit", "20", Instant.parse("2025-03-15T00:00:00Z"));
    persistTx("tx-f", "acct-4", "deposit", "30", Instant.parse("2025-04-01T00:00:00Z"));
    em.flush();
    em.clear();

    List<TransactionHistoryEntity> march = txRepo.findAccountActivityBetween(
        "acct-4",
        Instant.parse("2025-03-01T00:00:00Z"),
        Instant.parse("2025-03-31T23:59:59Z"));
    assertEquals(2, march.size());
  }

  @Test
  void accountHistoryCustomFindersResolve() {
    AccountHistoryEntity a1 = new AccountHistoryEntity(
        "acct-7", "cust-1", "12-34-56", "CHECKING", "CLOSED",
        Instant.parse("2020-01-01T00:00:00Z"),
        Instant.parse("2024-01-01T00:00:00Z"),
        "DB2");
    AccountHistoryEntity a2 = new AccountHistoryEntity(
        "acct-8", "cust-1", "12-34-56", "SAVINGS", "ACTIVE",
        Instant.parse("2021-06-01T00:00:00Z"),
        null, "DB2");
    AccountHistoryEntity a3 = new AccountHistoryEntity(
        "acct-9", "cust-2", "99-88-77", "CHECKING", "ACTIVE",
        Instant.parse("2022-01-01T00:00:00Z"),
        null, "DB2");
    em.persistAndFlush(a1);
    em.persistAndFlush(a2);
    em.persistAndFlush(a3);
    em.clear();

    List<AccountHistoryEntity> cust1 = acctRepo.findByCustomerId("cust-1");
    assertEquals(2, cust1.size());

    List<AccountHistoryEntity> closed = acctRepo.findByStatus("CLOSED");
    assertEquals(1, closed.size());
    assertEquals("acct-7", closed.get(0).getAccountId());
    assertTrue(closed.get(0).getClosedAt() != null);
  }

  private void persistTx(String id, String acct, String kind, String amt, Instant when) {
    em.persist(new TransactionHistoryEntity(
        id, acct, kind, new BigDecimal(amt), "USD", when, false, "DB2"));
  }
}

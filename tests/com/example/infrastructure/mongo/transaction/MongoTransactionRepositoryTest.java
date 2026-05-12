package com.example.infrastructure.mongo.transaction;

import com.example.domain.transaction.model.PostDepositCmd;
import com.example.domain.transaction.model.ReverseTransactionCmd;
import com.example.domain.transaction.model.TransactionAggregate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MongoTransactionRepositoryTest {

  private final TransactionMongoDataRepository data = mock(TransactionMongoDataRepository.class);
  private final MongoTransactionRepository repo = new MongoTransactionRepository(data);

  @Test
  void saveMapsAllFieldsIncludingReflectiveCurrency() {
    TransactionAggregate agg = new TransactionAggregate("tx-1");
    agg.execute(new PostDepositCmd("tx-1", "acct-1", new BigDecimal("125.50"), "USD"));
    agg.execute(new ReverseTransactionCmd("tx-1", "customer dispute"));

    repo.save(agg);

    TransactionDocument doc = repo.toDocument(agg);
    assertEquals("tx-1", doc.getId());
    assertEquals("acct-1", doc.getAccountId());
    assertEquals("deposit", doc.getKind());
    assertEquals(new BigDecimal("125.50"), doc.getAmount());
    assertEquals("USD", doc.getCurrency());
    assertTrue(doc.isPosted());
    assertTrue(doc.isReversed());
    assertEquals(2, doc.getVersion());
    verify(data).save(any(TransactionDocument.class));
  }

  @Test
  void findByIdRestoresStateIncludingCurrency() {
    TransactionDocument doc = new TransactionDocument();
    doc.setId("tx-2");
    doc.setAccountId("acct-2");
    doc.setKind("withdrawal");
    doc.setAmount(new BigDecimal("42.00"));
    doc.setCurrency("EUR");
    doc.setPosted(true);
    doc.setReversed(false);
    doc.setVersion(1);
    when(data.findById("tx-2")).thenReturn(Optional.of(doc));

    TransactionAggregate restored = repo.findById("tx-2").orElseThrow();
    assertEquals("tx-2", restored.id());
    assertEquals("acct-2", restored.getAccountId());
    assertEquals("withdrawal", restored.getKind());
    assertEquals(new BigDecimal("42.00"), restored.getAmount());
    assertTrue(restored.isPosted());
    assertFalse(restored.isReversed());
    assertEquals(1, restored.getVersion());
  }

  @Test
  void findByIdReturnsEmptyWhenMissing() {
    when(data.findById("nope")).thenReturn(Optional.empty());
    assertTrue(repo.findById("nope").isEmpty());
  }
}

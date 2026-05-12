package com.example.infrastructure.mongo.account;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MongoAccountRepositoryTest {

  private final AccountMongoDataRepository data = mock(AccountMongoDataRepository.class);
  private final MongoAccountRepository repo = new MongoAccountRepository(data);

  @Test
  void saveBuildsFullyPopulatedDocument() {
    AccountAggregate agg = new AccountAggregate("a-1");
    agg.execute(new OpenAccountCmd("a-1", "cust-1", "CHECKING", 100L, "12-34-56"));
    agg.execute(new UpdateAccountStatusCmd("a-1", "ACTIVE"));

    repo.save(agg);

    AccountDocument doc = repo.toDocument(agg);
    assertEquals("a-1", doc.getId());
    assertEquals("cust-1", doc.getCustomerId());
    assertEquals("CHECKING", doc.getAccountType());
    assertEquals(100L, doc.getInitialDeposit());
    assertEquals("12-34-56", doc.getSortCode());
    assertEquals("ACTIVE", doc.getStatus());
    assertTrue(doc.isOpened());
    assertFalse(doc.isClosed());
    assertEquals(2, doc.getVersion());
    verify(data).save(any(AccountDocument.class));
  }

  @Test
  void findByIdRestoresState() {
    AccountDocument doc = new AccountDocument();
    doc.setId("a-2");
    doc.setCustomerId("cust-9");
    doc.setAccountType("SAVINGS");
    doc.setInitialDeposit(500L);
    doc.setSortCode("99-88-77");
    doc.setStatus("CLOSED");
    doc.setOpened(true);
    doc.setClosed(true);
    doc.setVersion(5);
    when(data.findById("a-2")).thenReturn(Optional.of(doc));

    AccountAggregate restored = repo.findById("a-2").orElseThrow();
    assertEquals("a-2", restored.id());
    assertEquals("cust-9", restored.getCustomerId());
    assertEquals("SAVINGS", restored.getAccountType());
    assertEquals(500L, restored.getInitialDeposit());
    assertEquals("99-88-77", restored.getSortCode());
    assertEquals("CLOSED", restored.getStatus());
    assertTrue(restored.isOpened());
    assertTrue(restored.isClosed());
    assertEquals(5, restored.getVersion());
  }

  @Test
  void findByIdReturnsEmptyWhenMissing() {
    when(data.findById("missing")).thenReturn(Optional.empty());
    assertTrue(repo.findById("missing").isEmpty());
  }
}
